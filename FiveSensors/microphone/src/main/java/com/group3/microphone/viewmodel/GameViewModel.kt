package com.group3.microphone.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group3.microphone.CHARACTER_HEIGHT_FRACTION
import com.group3.microphone.SoundManager
import com.group3.microphone.CHARACTER_X_FRACTION
import com.group3.microphone.INITIAL_CHARACTER_Y
import com.group3.microphone.JUMP_MAX_AMPLITUDE
import com.group3.microphone.Platform
import com.group3.microphone.audio.MicAudioRecorder
import com.group3.microphone.detector.JumpDetector
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

private const val JUMP_VISUAL_DURATION_MS = 600L

// ── Horizontal scrolling ─────────────────────────────────────────────────────
private const val SCROLL_SPEED = 0.12f        // canvas-widths per second (world moves left)
private const val SPAWN_TRIGGER_X = 0.60f     // spawn next platform when rightmost crosses here
private const val PLATFORM_MIN_H_GAP = 0.08f  // min horizontal gap between platforms
private const val PLATFORM_MAX_H_GAP = 0.22f
private const val PLATFORM_MIN_WIDTH = 0.20f  // fraction of canvas width
private const val PLATFORM_MAX_WIDTH = 0.40f
private const val PLATFORM_MIN_Y = 0.35f      // fraction of canvas height (top of spawn range); kept below cloud zone
private const val PLATFORM_MAX_Y = 0.70f      // fraction of canvas height (bottom of spawn range)
private const val MIN_Y_SEPARATION = 0.12f    // consecutive platforms must differ by at least this

// ── Character physics — all tunable ─────────────────────────────────────────
private const val GRAVITY = 0.70f            // canvas-heights / s²
private const val MAX_FALL_SPEED = 1.50f     // terminal velocity
private const val JUMP_VELOCITY_BASE = 0.28f // minimum upward speed on any jump
private const val JUMP_VELOCITY_SCALE = 0.55f// extra speed proportional to voice amplitude

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("voicejump_prefs", Context.MODE_PRIVATE)
    private val recorder = MicAudioRecorder()
    private val detector = JumpDetector()
    private val random = Random(System.currentTimeMillis())
    private var nextPlatformId = 0
    private var lastPlatformY = INITIAL_CHARACTER_Y + CHARACTER_HEIGHT_FRACTION

    // ── Audio state ──────────────────────────────────────────────────────────
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude

    private val _jumpAmplitude = MutableStateFlow(0f)
    val jumpAmplitude: StateFlow<Float> = _jumpAmplitude

    // ── Game world state ─────────────────────────────────────────────────────
    private val _platforms = MutableStateFlow<List<Platform>>(emptyList())
    val platforms: StateFlow<List<Platform>> = _platforms

    /** Screen-Y fraction of the character's top edge. */
    private val _characterY = MutableStateFlow(INITIAL_CHARACTER_Y)
    val characterY: StateFlow<Float> = _characterY

    private val _isGameOver = MutableStateFlow(false)
    val isGameOver: StateFlow<Boolean> = _isGameOver

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _bestScore = MutableStateFlow(prefs.getInt("best_score", 0))
    val bestScore: StateFlow<Int> = _bestScore

    private var characterVelocityY = 0f
    private var characterLandedPlatformId: Int? = null
    private val scoredPlatformIds = mutableSetOf<Int>()
    private val passedPlatformIds = mutableSetOf<Int>()

    private var listeningJob: Job? = null
    private var resetJob: Job? = null
    private var platformJob: Job? = null

    // ── Public API ───────────────────────────────────────────────────────────

    @SuppressLint("MissingPermission")
    fun startListening() {
        if (_isListening.value) return
        _isListening.value = true
        listeningJob = viewModelScope.launch {
            val sharedAmplitude = recorder.amplitudeFlow()
                .shareIn(this, SharingStarted.Eagerly, replay = 0)
            // Volume bar always shows real amplitude; detector only sees it when mic isn't muted
            launch { sharedAmplitude.collect { _amplitude.value = it } }
            detector.detectJumps(sharedAmplitude.filter { !SoundManager.isMicMuted }).collect { amplitude ->
                resetJob?.cancel()
                _jumpAmplitude.value = amplitude
                val force = JUMP_VELOCITY_BASE +
                        (amplitude / JUMP_MAX_AMPLITUDE).coerceIn(0f, 1f) * JUMP_VELOCITY_SCALE
                characterVelocityY = -force
                characterLandedPlatformId = null
                SoundManager.playJump()
                resetJob = launch {
                    delay(JUMP_VISUAL_DURATION_MS)
                    _jumpAmplitude.value = 0f
                }
            }
        }
    }

    fun startPlatforms() {
        if (platformJob?.isActive == true) return
        _platforms.value = buildInitialPlatforms()
        platformJob = viewModelScope.launch {
            var lastMs = System.currentTimeMillis()
            while (isActive) {
                delay(16L)
                val now = System.currentTimeMillis()
                val delta = (now - lastMs).coerceAtMost(50L) / 1000f
                lastMs = now
                if (!_isGameOver.value && !_isPaused.value) {
                    tickPlatforms(delta)
                    tickCharacter(_platforms.value, delta)
                }
            }
        }
    }

    fun pause() {
        _isPaused.value = true
        stopListening()
    }

    fun resume() {
        _isPaused.value = false
    }

    fun resetGame() {
        stopPlatforms()
        startPlatforms()
    }

    fun stopListening() {
        listeningJob?.cancel()
        listeningJob = null
        _isListening.value = false
        _amplitude.value = 0f
        _jumpAmplitude.value = 0f
    }

    /**
     * Maps the Settings sensitivity slider (0..1) to a jump threshold.
     * 0.0 → 0.016 (needs a clear voice), 0.5 → 0.010 (default), 1.0 → 0.004 (soft voice).
     */
    fun setSensitivity(sensitivity: Float) {
        detector.threshold = (0.016f - 0.012f * sensitivity).coerceIn(0.004f, 0.016f)
    }

    private fun checkAndSaveBestScore(score: Int) {
        if (score > _bestScore.value) {
            _bestScore.value = score
            prefs.edit().putInt("best_score", score).apply()
        }
    }

    fun stopPlatforms() {
        platformJob?.cancel()
        platformJob = null
        _platforms.value = emptyList()
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
        stopPlatforms()
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /** Starting platform under the character, then stepping stones spread left→right. */
    private fun buildInitialPlatforms(): List<Platform> {
        nextPlatformId = 0
        _characterY.value = INITIAL_CHARACTER_Y
        characterVelocityY = 0f
        _isGameOver.value = false
        _isPaused.value = false
        _score.value = 0
        passedPlatformIds.clear()
        scoredPlatformIds.clear()
        val startY = INITIAL_CHARACTER_Y + CHARACTER_HEIGHT_FRACTION
        lastPlatformY = startY

        val startWidth = 0.32f
        val startPlatform = Platform(
            id = nextPlatformId++,
            x = 0.02f,
            yFraction = startY,
            widthFraction = startWidth
        )
        characterLandedPlatformId = startPlatform.id

        val list = mutableListOf(startPlatform)
        var x = startPlatform.x + startWidth + nextHGap()
        while (x < 1.3f) {
            val p = newPlatform(x)
            list.add(p)
            x += p.widthFraction + nextHGap()
        }
        return list
    }

    /** Scroll platforms left only while the character is airborne — voice drives all progress. */
    private fun tickPlatforms(deltaSeconds: Float) {
        val scrollDelta = if (characterLandedPlatformId == null) SCROLL_SPEED * deltaSeconds else 0f
        val moved = _platforms.value
            .map { it.copy(x = it.x - scrollDelta) }
            .filter { it.x + it.widthFraction > 0f }

        val result = moved.toMutableList()
        val rightmost = moved.maxByOrNull { it.x }
        if (rightmost == null || rightmost.x < SPAWN_TRIGGER_X) {
            val gap = nextHGap()
            val spawnX = if (rightmost != null)
                (rightmost.x + rightmost.widthFraction + gap).coerceAtLeast(1.0f)
            else 1.0f
            result.add(newPlatform(spawnX))
        }
        _platforms.value = result
    }

    /** Gravity, landing, and game-over check. All in screen-Y coordinates. */
    private fun tickCharacter(platforms: List<Platform>, deltaSeconds: Float) {
        // Stay pinned to landed platform until it scrolls past the character's X.
        val landed = characterLandedPlatformId?.let { id -> platforms.find { it.id == id } }
        if (landed != null) {
            if (CHARACTER_X_FRACTION in landed.x..(landed.x + landed.widthFraction)) {
                _characterY.value = landed.yFraction - CHARACTER_HEIGHT_FRACTION
                characterVelocityY = 0f
                return
            }
            characterLandedPlatformId = null
        }

        characterVelocityY = (characterVelocityY + GRAVITY * deltaSeconds)
            .coerceAtMost(MAX_FALL_SPEED)

        // prevY = position at END of the previous tick (written there by _characterY.value = newY,
        // or by the pinned/landing writes). Updated every frame that reaches this point.
        val prevY     = _characterY.value
        val newY      = prevY + characterVelocityY * deltaSeconds
        val prevBottom = prevY + CHARACTER_HEIGHT_FRACTION
        val newBottom  = newY  + CHARACTER_HEIGHT_FRACTION

        // Pass 1 — mark every platform whose top is already above (lower yFraction than) the
        // character's current bottom as permanently ineligible for landing this flight.
        // Runs unconditionally (rising OR falling) so that rising-through-a-platform is caught.
        platforms.forEach { p ->
            if (CHARACTER_X_FRACTION >= p.x && CHARACTER_X_FRACTION <= p.x + p.widthFraction) {
                if (prevBottom > p.yFraction) {
                    if (passedPlatformIds.add(p.id)) {   // add() returns true on first insertion
                        Log.d(TAG, "passed  p#${p.id} top=${p.yFraction} | prevBot=$prevBottom vel=$characterVelocityY")
                    }
                }
            }
        }

        // Pass 2 — landing check: only when falling, only against ALL platforms not yet passed,
        // picking the topmost (smallest yFraction) platform the character's bottom just crossed.
        // Condition: character bottom was AT OR ABOVE platform top last frame (prevBottom <= yFrac)
        //            AND is now AT OR BELOW platform top this frame (newBottom >= yFrac).
        if (characterVelocityY > 0f) {
            val hit = platforms
                .filter { p ->
                    p.id !in passedPlatformIds &&
                    CHARACTER_X_FRACTION >= p.x && CHARACTER_X_FRACTION <= p.x + p.widthFraction &&
                    prevBottom <= p.yFraction &&    // was above (or at) the top last frame
                    newBottom  >= p.yFraction       // is now at or below the top this frame
                }
                .minByOrNull { it.yFraction }       // land on the highest (smallest-Y) candidate

            if (hit != null) {
                Log.d(TAG,
                    "LAND    p#${hit.id} top=${hit.yFraction} | " +
                    "prevBot=$prevBottom newBot=$newBottom vel=$characterVelocityY passed=$passedPlatformIds"
                )
                _characterY.value     = hit.yFraction - CHARACTER_HEIGHT_FRACTION
                characterVelocityY    = 0f
                characterLandedPlatformId = hit.id
                passedPlatformIds.clear()
                SoundManager.playLand()
                if (hit.id != 0 && scoredPlatformIds.add(hit.id)) {
                    _score.value++
                }
                return
            }
        }

        _characterY.value = newY
        if (newY > 1.05f) {
            _isGameOver.value = true
            checkAndSaveBestScore(_score.value)
            SoundManager.playGameOver()
        }
    }

    companion object {
        private const val TAG = "GameVM"
    }

    /** Random-height platform; consecutive platforms always differ in Y by at least MIN_Y_SEPARATION. */
    private fun newPlatform(x: Float): Platform {
        val width = PLATFORM_MIN_WIDTH + random.nextFloat() * (PLATFORM_MAX_WIDTH - PLATFORM_MIN_WIDTH)
        var y: Float
        var attempts = 0
        do {
            y = PLATFORM_MIN_Y + random.nextFloat() * (PLATFORM_MAX_Y - PLATFORM_MIN_Y)
            attempts++
        } while (abs(y - lastPlatformY) < MIN_Y_SEPARATION && attempts < 10)
        lastPlatformY = y
        return Platform(id = nextPlatformId++, x = x, yFraction = y, widthFraction = width)
    }

    private fun nextHGap(): Float =
        PLATFORM_MIN_H_GAP + random.nextFloat() * (PLATFORM_MAX_H_GAP - PLATFORM_MIN_H_GAP)
}

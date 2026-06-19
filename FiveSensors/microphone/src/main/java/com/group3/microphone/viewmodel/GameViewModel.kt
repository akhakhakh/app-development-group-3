package com.group3.microphone.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group3.microphone.CHARACTER_HEIGHT_FRACTION
import com.group3.microphone.CHARACTER_X_FRACTION
import com.group3.microphone.DifficultyConfig
import com.group3.microphone.DifficultyScaler
import com.group3.microphone.INITIAL_CHARACTER_Y
import com.group3.microphone.JUMP_MAX_AMPLITUDE
import com.group3.microphone.Platform
import com.group3.microphone.SoundManager
import com.group3.microphone.audio.MicAudioRecorder
import com.group3.microphone.detector.JumpDetector
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val JUMP_VISUAL_DURATION_MS = 600L

// ── Spawn trigger (not difficulty-dependent) ─────────────────────────────────
private const val SPAWN_TRIGGER_X = 0.60f     // spawn next platform when rightmost crosses here

// ── Character physics — all tunable ─────────────────────────────────────────
private const val GRAVITY            = 0.70f   // canvas-heights / s²
private const val MAX_FALL_SPEED     = 1.50f   // terminal velocity
private const val JUMP_VELOCITY_BASE = 0.28f   // minimum upward speed on any jump
private const val JUMP_VELOCITY_SCALE = 0.55f  // extra speed proportional to voice amplitude

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

        // Always spawn initial platforms at the easiest difficulty (score = 0).
        val diff = DifficultyScaler.forScore(0)
        val startWidth = 0.32f
        val startPlatform = Platform(
            id = nextPlatformId++,
            x = 0.02f,
            yFraction = startY,
            widthFraction = startWidth
        )
        characterLandedPlatformId = startPlatform.id

        val list = mutableListOf(startPlatform)
        var x = startPlatform.x + startWidth + nextHGap(diff)
        while (x < 1.3f) {
            val p = newPlatform(x, diff)
            list.add(p)
            x += p.widthFraction + nextHGap(diff)
        }
        return list
    }

    /**
     * Scroll platforms left, advance moving-platform oscillation, score passed platforms,
     * and spawn new platforms at the right edge.
     */
    private fun tickPlatforms(deltaSeconds: Float) {
        val diff = DifficultyScaler.forScore(_score.value)
        val scrollDelta = if (characterLandedPlatformId == null) diff.scrollSpeed * deltaSeconds else 0f

        // Move every platform left by scrollDelta; also advance oscillation phase for moving platforms.
        val moved = _platforms.value.map { p ->
            val newScrollX = p.scrollX - scrollDelta
            if (p.moveAmplitude == 0f) {
                p.copy(x = newScrollX, scrollX = newScrollX)
            } else {
                val newPhase = p.movePhase + p.moveSpeed * deltaSeconds
                val newX = newScrollX + sin(newPhase.toDouble()).toFloat() * p.moveAmplitude
                p.copy(x = newX, scrollX = newScrollX, movePhase = newPhase)
            }
        // Keep platforms that are still (or could still oscillate into) the visible area.
        }.filter { it.scrollX + it.widthFraction + it.moveAmplitude > 0f }

        val result = moved.toMutableList()
        val rightmost = moved.maxByOrNull { it.scrollX }
        if (rightmost == null || rightmost.scrollX < SPAWN_TRIGGER_X) {
            val gap = nextHGap(diff)
            val spawnX = if (rightmost != null)
                (rightmost.scrollX + rightmost.widthFraction + gap).coerceAtLeast(1.0f)
            else 1.0f
            result.add(newPlatform(spawnX, diff))
        }
        _platforms.value = result
    }

    /** Gravity, landing, and game-over check. All in screen-Y coordinates. */
    private fun tickCharacter(platforms: List<Platform>, deltaSeconds: Float) {
        // Stay pinned to landed platform until its visible x edge scrolls past the character.
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

        val prevY      = _characterY.value
        val newY       = prevY + characterVelocityY * deltaSeconds
        val prevBottom = prevY  + CHARACTER_HEIGHT_FRACTION
        val newBottom  = newY   + CHARACTER_HEIGHT_FRACTION

        // Pass 1 — mark every platform whose top is already below (larger yFraction than)
        // the character's current bottom as ineligible for landing on this flight.
        platforms.forEach { p ->
            if (CHARACTER_X_FRACTION >= p.x && CHARACTER_X_FRACTION <= p.x + p.widthFraction) {
                if (prevBottom > p.yFraction) {
                    if (passedPlatformIds.add(p.id)) {
                        Log.d(TAG, "passed  p#${p.id} top=${p.yFraction} | prevBot=$prevBottom vel=$characterVelocityY")
                    }
                }
            }
        }

        // Pass 2 — landing check: only when falling, topmost eligible platform that
        // the character's bottom just crossed.
        if (characterVelocityY > 0f) {
            val hit = platforms
                .filter { p ->
                    p.id !in passedPlatformIds &&
                    CHARACTER_X_FRACTION >= p.x && CHARACTER_X_FRACTION <= p.x + p.widthFraction &&
                    prevBottom <= p.yFraction &&
                    newBottom  >= p.yFraction
                }
                .minByOrNull { it.yFraction }

            if (hit != null) {
                Log.d(TAG,
                    "LAND    p#${hit.id} top=${hit.yFraction} | " +
                    "prevBot=$prevBottom newBot=$newBottom vel=$characterVelocityY passed=$passedPlatformIds"
                )
                _characterY.value          = hit.yFraction - CHARACTER_HEIGHT_FRACTION
                characterVelocityY         = 0f
                characterLandedPlatformId  = hit.id
                passedPlatformIds.clear()
                SoundManager.playLand()
                if (hit.id != 0 && scoredPlatformIds.add(hit.id)) {
                    _score.value += 1
                }
                return
            }
        }

        _characterY.value = newY
        if (newY > 1.05f) {
            _isGameOver.value = true
            stopListening()
            checkAndSaveBestScore(_score.value)
            SoundManager.playGameOver()
        }
    }

    companion object {
        private const val TAG = "GameVM"
    }

    /**
     * Spawn a new platform at [x] using the current difficulty config.
     * Platforms at tier 3+ have a chance to oscillate horizontally (moving platforms).
     */
    private fun newPlatform(x: Float, diff: DifficultyConfig): Platform {
        val width = diff.platformMinWidth +
                random.nextFloat() * (diff.platformMaxWidth - diff.platformMinWidth)
        var y: Float
        var attempts = 0
        do {
            y = diff.platformMinY + random.nextFloat() * (diff.platformMaxY - diff.platformMinY)
            attempts++
        } while (abs(y - lastPlatformY) < diff.minYSeparation && attempts < 10)
        lastPlatformY = y

        val isMoving = diff.movingPlatformChance > 0f &&
                random.nextFloat() < diff.movingPlatformChance
        return if (isMoving) {
            val phase = random.nextFloat() * 2f * PI.toFloat()
            Platform(
                id            = nextPlatformId++,
                x             = x + sin(phase.toDouble()).toFloat() * diff.movingPlatformAmplitude,
                yFraction     = y,
                widthFraction = width,
                scrollX       = x,
                moveAmplitude = diff.movingPlatformAmplitude,
                moveSpeed     = diff.movingPlatformSpeed,
                movePhase     = phase
            )
        } else {
            Platform(id = nextPlatformId++, x = x, yFraction = y, widthFraction = width)
        }
    }

    private fun nextHGap(diff: DifficultyConfig): Float =
        diff.platformMinHGap + random.nextFloat() * (diff.platformMaxHGap - diff.platformMinHGap)
}

package com.group3.touchscreen2p.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group3.touchscreen2p.Constants
import com.group3.touchscreen2p.model.FloatingEffect
import com.group3.touchscreen2p.model.GameState
import com.group3.touchscreen2p.model.Phase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.group3.touchscreen2p.model.Target
import com.group3.touchscreen2p.model.TargetType
import kotlin.math.pow
import kotlin.math.roundToInt

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var countdownJob: Job? = null
    private var gameLoopJob: Job? = null
    private var spawnJob: Job? = null

    private var pauseStartMs: Long = 0

    fun pauseGame() {
        if (_state.value.phase != Phase.PLAYING) return
        pauseStartMs = SystemClock.elapsedRealtime()
        gameLoopJob?.cancel()
        spawnJob?.cancel()
        _state.update { it.copy(phase = Phase.PAUSED) }
    }

    fun resumeGame() {
        if (_state.value.phase != Phase.PAUSED) return
        val pausedDurationMs = SystemClock.elapsedRealtime() - pauseStartMs
        _state.update { s ->
            s.copy(
                phase = Phase.PLAYING,
                targets = s.targets.map { it.copy(spawnTimeMs = it.spawnTimeMs + pausedDurationMs) },
                floatingEffects = s.floatingEffects.map { it.copy(startTimeMs =
                    it.startTimeMs + pausedDurationMs) },
                comboWindowEndMs1 = if (s.combo1 > 0) s.comboWindowEndMs1 + pausedDurationMs else s.comboWindowEndMs1,
                comboWindowEndMs2 = if (s.combo2 > 0) s.comboWindowEndMs2 + pausedDurationMs else s.comboWindowEndMs2
            )
        }
        launchGameLoop()
        launchSpawner()
    }

    private fun cancelAll() {
        countdownJob?.cancel()
        gameLoopJob?.cancel()
        spawnJob?.cancel()
    }

    fun startGame() {
        cancelAll()
        _state.value = GameState()
        launchCountdown()
    }

    private fun launchCountdown() {
        countdownJob = viewModelScope.launch {
            for (tick in Constants.COUNTDOWN_SECONDS downTo 1) {
                _state.update { it.copy(countdownValue = tick) }
                delay(1_000)
            }
            _state.update { it.copy(phase = Phase.PLAYING, countdownValue = 0) }
            launchGameLoop()
            launchSpawner()
        }
    }

    private fun launchGameLoop() {
        gameLoopJob = viewModelScope.launch {
            while (isActive) {
                // Gives the current time in milliseconds since the phone booted, instead of
                // System.currentTimeMillis() because it's not affected by the user changing their clock.
                val now = SystemClock.elapsedRealtime()
                _state.update { s ->
                    val aliveTargets = s.targets.mapNotNull { t ->
                        val p =
                            1f - ((now - t.spawnTimeMs) / t.lifetimeMs.toFloat()).coerceIn(0f, 1f)
                        if (p > 0f) t.copy(progress = p) else null
                    }
                    val aliveEffects = s.floatingEffects.filter { e ->
                        now - e.startTimeMs < Constants.FLOATING_EFFECT_DURATION_MS
                    }
                    val combo1Expired = s.combo1 > 0 && now > s.comboWindowEndMs1
                    val combo2Expired = s.combo2 > 0 && now > s.comboWindowEndMs2

                    s.copy(
                        targets = aliveTargets,
                        floatingEffects = aliveEffects,
                        combo1 = if (combo1Expired) 0 else s.combo1,
                        combo2 = if (combo2Expired) 0 else s.combo2
                    )
                }
                delay(Constants.GAME_TICK_MS)
            }
        }
    }


    private fun launchSpawner() {
        spawnJob = viewModelScope.launch {
            while (isActive) {
                delay(Constants.SPAWN_INTERVAL_MS)
                if (_state.value.phase == Phase.PLAYING) {
                    spawnTargets()
                }
            }
        }
    }

    private fun spawnTargets() {
        val now = SystemClock.elapsedRealtime()
        _state.update { s ->
            var list = s.targets
            val specialsEnabled = maxOf(s.score1, s.score2) >= Constants.SPECIALS_UNLOCK_SCORE
            val leadingScore = maxOf(s.score1, s.score2)
            val maxTargets = currentMaxTargetsPerPlayer(leadingScore)

            if (list.count { it.player == 1 } < maxTargets) {
                list = list + buildTarget(player = 1, now = now, specialsEnabled =
                    specialsEnabled, existing = list, leadingScore = leadingScore)
            }
            if (list.count { it.player == 2 } < maxTargets) {
                list = list + buildTarget(player = 2, now = now, specialsEnabled =
                    specialsEnabled, existing = list, leadingScore = leadingScore)
            }

            s.copy(targets = list)
        }
    }

    private fun currentMaxTargetsPerPlayer(leadingScore: Int): Int {
        if (leadingScore <= Constants.MAX_TARGETS_RAMP_START_SCORE) {
            return Constants.MAX_TARGETS_PER_PLAYER
        }
        val t = ((leadingScore -
                Constants.MAX_TARGETS_RAMP_START_SCORE).toFloat() /
                (Constants.MAX_TARGETS_RAMP_END_SCORE -
                        Constants.MAX_TARGETS_RAMP_START_SCORE).toFloat())
            .coerceIn(0f, 1f)
        val cap = Constants.MAX_TARGETS_PER_PLAYER +
                (Constants.MAX_TARGETS_PER_PLAYER_LATE -
                        Constants.MAX_TARGETS_PER_PLAYER) * t
        return cap.roundToInt()
    }

    private fun buildTarget(player: Int, now: Long, specialsEnabled: Boolean,
                            existing: List<Target>, leadingScore: Int): Target {
        val yMin = if (player == 1) Constants.SPAWN_Y_P1_MIN else Constants.SPAWN_Y_P2_MIN
        val yMax = if (player == 1) Constants.SPAWN_Y_P1_MAX else Constants.SPAWN_Y_P2_MAX
        var x: Float
        var y: Float
        var attempts = 0
        val lifetime = if (leadingScore <
            Constants.LIFETIME_REDUCTION_START_SCORE) {
            Constants.TARGET_LIFETIME_MS
        } else {
            val t = (leadingScore - Constants.LIFETIME_REDUCTION_START_SCORE).toFloat() /
                    (Constants.WIN_SCORE - Constants.LIFETIME_REDUCTION_START_SCORE).toFloat()
            val curvedT = t.pow(Constants.LIFETIME_SHRINK_EXPONENT)
            (Constants.TARGET_LIFETIME_MS - (Constants.TARGET_LIFETIME_MS
                    - Constants.TARGET_LIFETIME_MIN_MS) * curvedT)
                .toLong()
                .coerceAtLeast(Constants.TARGET_LIFETIME_MIN_MS)
        }
        do {
            x = Random.nextFloat() * (Constants.SPAWN_X_MAX - Constants.SPAWN_X_MIN)+
                    Constants.SPAWN_X_MIN
            y = Random.nextFloat() * (yMax - yMin) + yMin
            attempts++
        } while (attempts < 10 && existing.any { t ->
                val dx = x - t.normalizedX
                val dy = y - t.normalizedY
                dx * dx + dy * dy < Constants.TARGET_MIN_SPACING * Constants.TARGET_MIN_SPACING
            })
        val type = if (!specialsEnabled) { TargetType.BULLSEYE
        } else {
            when (Random.nextInt(10)) {
                in 0..6 -> TargetType.BULLSEYE // 70%
                in 7..8 -> TargetType.TRICK // 20%
                else -> TargetType.BOMB // 10%
            }
        }

        return Target(
            player = player, normalizedX = x, normalizedY = y, spawnTimeMs = now, lifetimeMs =
                lifetime, type = type
        )
    }

    fun onTargetHit(player: Int, target: Target, normX: Float, normY: Float) {
        if (_state.value.phase != Phase.PLAYING) return
        val now = SystemClock.elapsedRealtime()
        _state.update { s ->
            val oldCombo = if (player == 1) s.combo1 else s.combo2
            val isCorrect = target.type == TargetType.BULLSEYE
            val newCombo = if (isCorrect) oldCombo + 1 else 0
            val comboActive = newCombo >= Constants.COMBO_THRESHOLD

            val basePoints = when (target.type) {
                TargetType.BULLSEYE -> Constants.POINTS_BULLSEYE
                TargetType.TRICK -> Constants.POINTS_TRICK
                TargetType.BOMB -> Constants.POINTS_BOMB
            }
            val points = if (isCorrect && comboActive) basePoints *
                    Constants.COMBO_MULTIPLIER else basePoints

            val newScore1 = if (player == 1) (s.score1 +
                    points).coerceAtLeast(Constants.MIN_SCORE) else s.score1
            val newScore2 = if (player == 2) (s.score2 +
                    points).coerceAtLeast(Constants.MIN_SCORE) else s.score2
            val newTargets = s.targets.filter { it.id != target.id }

            val effect = FloatingEffect(
                text = when {
                    target.type == TargetType.TRICK -> "-1 TRICK!"
                    target.type == TargetType.BOMB -> "-2 BOOM!"
                    comboActive -> "+$points COMBO!"
                    else -> "+$points"
                },
                normalizedX = normX,
                normalizedY = normY,
                player = player,
                type = target.type,
                startTimeMs = now
            )

            val newPhase = when {
                newScore1 >= Constants.WIN_SCORE -> Phase.GAME_OVER
                newScore2 >= Constants.WIN_SCORE -> Phase.GAME_OVER
                else -> s.phase
            }

            val newWinner = when {
                newScore1 >= Constants.WIN_SCORE -> 1
                newScore2 >= Constants.WIN_SCORE -> 2
                else -> 0
            }

            val newCombo1 = if (player == 1) newCombo else s.combo1
            val newCombo2 = if (player == 2) newCombo else s.combo2
            val newComboWindowEndMs1 = if (player == 1 && isCorrect) now +
                    Constants.COMBO_WINDOW_MS else s.comboWindowEndMs1
            val newComboWindowEndMs2 = if (player == 2 && isCorrect) now +
                    Constants.COMBO_WINDOW_MS else s.comboWindowEndMs2

            s.copy(
                score1 = newScore1,
                score2 = newScore2,
                targets = newTargets,
                floatingEffects = s.floatingEffects + effect,
                phase = newPhase,
                winner = newWinner,
                combo1 = newCombo1,
                combo2 = newCombo2,
                comboWindowEndMs1 = newComboWindowEndMs1,
                comboWindowEndMs2 = newComboWindowEndMs2,
                bestCombo1 = maxOf(s.bestCombo1, newCombo1),
                bestCombo2 = maxOf(s.bestCombo2, newCombo2)
            )
        }

        if (_state.value.phase == Phase.GAME_OVER) cancelAll()
    }


    override fun onCleared() {
        super.onCleared()
        cancelAll()
    }
}
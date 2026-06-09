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

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var countdownJob: Job? = null
    private var gameLoopJob: Job? = null
    private var spawnJob: Job? = null

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
                    s.copy(targets = aliveTargets, floatingEffects = aliveEffects)
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

            if (list.count { it.player == 1 } < Constants.MAX_TARGETS_PER_PLAYER) {
                list = list + buildTarget(player = 1, now = now)
            }
            if (list.count { it.player == 2 } < Constants.MAX_TARGETS_PER_PLAYER) {
                list = list + buildTarget(player = 2, now = now)
            }

            s.copy(targets = list)
        }
    }

    private fun buildTarget(player: Int, now: Long): Target {
        val x = Random.nextFloat() * (Constants.SPAWN_X_MAX - Constants.SPAWN_X_MIN) + Constants.SPAWN_X_MIN
        val yMin = if (player == 1) Constants.SPAWN_Y_P1_MIN else Constants.SPAWN_Y_P2_MIN
        val yMax = if (player == 1) Constants.SPAWN_Y_P1_MAX else Constants.SPAWN_Y_P2_MAX
        val y = Random.nextFloat() * (yMax - yMin) + yMin
        val type = when (Random.nextInt(10)) {
            in 0..6 -> TargetType.BULLSEYE // 70%
            in 7..8 -> TargetType.TRICK // 20%
            else -> TargetType.BOMB // 10%
        }

        return Target(
            player = player, normalizedX = x, normalizedY = y, spawnTimeMs = now, type = type
        )
    }

    fun onTargetHit(player: Int, target: Target, normX: Float, normY: Float) {
        if (_state.value.phase != Phase.PLAYING) return

        _state.update { s ->
            val points = when (target.type) {
                TargetType.BULLSEYE -> Constants.POINTS_BULLSEYE
                TargetType.TRICK -> Constants.POINTS_TRICK
                TargetType.BOMB -> Constants.POINTS_BOMB
            }

            val newScore1 = if (player == 1) (s.score1 + points).coerceAtLeast(Constants.MIN_SCORE)
            else s.score1

            val newScore2 = if (player == 2) (s.score2 + points).coerceAtLeast(Constants.MIN_SCORE)
            else s.score2

            val newTargets = s.targets.filter { it.id != target.id }

            val effect = FloatingEffect(
                text = when (target.type) {
                    TargetType.TRICK -> "-1 TRICK!"
                    TargetType.BOMB -> "-2 BOOM!"
                    TargetType.BULLSEYE -> "+1"
                },
                normalizedX = normX,
                normalizedY = normY,
                player = player,
                type = target.type,
                startTimeMs = SystemClock.elapsedRealtime()
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

            s.copy(
                score1 = newScore1,
                score2 = newScore2,
                targets = newTargets,
                floatingEffects = s.floatingEffects + effect,
                phase = newPhase,
                winner = newWinner
            )
        }

        if (_state.value.phase == Phase.GAME_OVER) cancelAll()
    }


    override fun onCleared() {
        super.onCleared()
        cancelAll()
    }
}
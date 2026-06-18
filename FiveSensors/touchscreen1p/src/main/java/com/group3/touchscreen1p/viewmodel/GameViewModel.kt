package com.group3.touchscreen1p.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group3.touchscreen1p.model.FallingOrb
import com.group3.touchscreen1p.model.GameState
import com.group3.touchscreen1p.model.OrbColor
import com.group3.touchscreen1p.model.DifficultyLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    private var orbSpeed = 8f
    private var spawnDelay = 1200L
    private var bestScore = 0
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var nextOrbId = 0L

    init {
        startSpawning()
        startGameLoop()
    }

    private fun startSpawning() {
        viewModelScope.launch {

            while (true) {

                delay(spawnDelay)

                if (_gameState.value.isPaused ||
                    _gameState.value.isGameOver
                ) continue

                spawnOrb()
            }
        }
    }

    private fun spawnOrb() {

        val newOrb = FallingOrb(
            id = nextOrbId++,
            lane = Random.nextInt(0, 4),
            color = OrbColor.entries.random(),
            positionY = 0f
        )

        _gameState.value = _gameState.value.copy(
            orbs = _gameState.value.orbs + newOrb
        )
    }

    private fun startGameLoop() {

        viewModelScope.launch {

            while (true) {

                delay(16L)

                if (_gameState.value.isPaused ||
                    _gameState.value.isGameOver
                ) continue

                updateOrbs()
            }
        }
    }

    private fun updateOrbs() {

        val movedOrbs = _gameState.value.orbs.map {
            it.copy(
                positionY = it.positionY + orbSpeed
            )
        }

        val missedOrbs = movedOrbs.filter {
            it.positionY > 1500f
        }

        val remainingOrbs = movedOrbs.filter {
            it.positionY <= 1500f
        }

        var currentLives = _gameState.value.lives

        if (missedOrbs.isNotEmpty()) {
            currentLives -= missedOrbs.size
        }

        currentLives = currentLives.coerceAtLeast(0)

        _gameState.value = _gameState.value.copy(
            lives = currentLives,
            combo = if (missedOrbs.isNotEmpty()) 1 else _gameState.value.combo,
            isGameOver = currentLives <= 0,
            orbs = remainingOrbs
        )
    }

    private fun loseLife() {

        val newLives =
            (_gameState.value.lives - 1)
                .coerceAtLeast(0)

        _gameState.value =
            _gameState.value.copy(
                lives = newLives,
                combo = 1,
                isGameOver = newLives <= 0
            )
    }

    fun hitColor(
        color: OrbColor,
        lane: Int
    ) {

        val orb = _gameState.value.orbs.firstOrNull {

            it.color == color &&
                    it.lane == lane &&
                    it.positionY in 1100f..1400f
        }

        if (orb != null) {

            val combo =
                (_gameState.value.combo + 1)
                    .coerceAtMost(10)

            val newScore =
                _gameState.value.score +
                        (10 * _gameState.value.combo)

            bestScore = maxOf(bestScore, newScore)

            _gameState.value =
                _gameState.value.copy(

                    score = newScore,

                    combo = combo,

                    orbs =
                        _gameState.value.orbs.filter {
                            it.id != orb.id
                        }
                )

        } else {

            loseLife()
        }
    }

    fun pauseGame() {

        _gameState.value = _gameState.value.copy(
            isPaused = !_gameState.value.isPaused
        )
    }

    fun restartGame() {

        nextOrbId = 0

        _gameState.value = GameState()
    }

    fun setDifficulty(
        difficulty: DifficultyLevel
    ) {

        when(difficulty) {

            DifficultyLevel.EASY -> {
                orbSpeed = 5f
                spawnDelay = 1500L
            }

            DifficultyLevel.MEDIUM -> {
                orbSpeed = 8f
                spawnDelay = 1200L
            }

            DifficultyLevel.HARD -> {
                orbSpeed = 12f
                spawnDelay = 800L
            }
        }
    }
}
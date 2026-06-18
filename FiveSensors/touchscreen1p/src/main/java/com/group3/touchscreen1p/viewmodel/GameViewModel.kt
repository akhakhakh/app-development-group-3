package com.group3.touchscreen1p.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group3.touchscreen1p.model.FallingOrb
import com.group3.touchscreen1p.model.GameState
import com.group3.touchscreen1p.model.OrbColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var nextOrbId = 0L

    init {
        startSpawning()
        startGameLoop()
    }

    private fun startSpawning() {
        viewModelScope.launch {

            while (true) {

                delay(1200)

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

        val moved = _gameState.value.orbs.map {

            it.copy(
                positionY = it.positionY + 8f
            )
        }

        val missed = moved.filter {
            it.positionY > 1500f
        }

        var lives = _gameState.value.lives

        if (missed.isNotEmpty()) {
            lives -= missed.size
        }

        _gameState.value = _gameState.value.copy(
            lives = lives.coerceAtLeast(0),
            combo = if (missed.isNotEmpty()) 1 else _gameState.value.combo,
            isGameOver = lives <= 0,
            orbs = moved.filter {
                it.positionY <= 1500f
            }
        )
    }

    fun hitColor(color: OrbColor) {

        val orb = _gameState.value.orbs.firstOrNull {

            it.color == color &&
                    it.positionY in 1100f..1400f
        }

        if (orb != null) {

            val combo =
                (_gameState.value.combo + 1)
                    .coerceAtMost(10)

            _gameState.value =
                _gameState.value.copy(

                    score =
                        _gameState.value.score +
                                (10 * _gameState.value.combo),

                    combo = combo,

                    orbs =
                        _gameState.value.orbs.filter {
                            it.id != orb.id
                        }
                )

        } else {

            _gameState.value =
                _gameState.value.copy(
                    combo = 1
                )
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
}
package com.group3.touchscreen1p.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.group3.touchscreen1p.manager.DifficultyManager
import com.group3.touchscreen1p.manager.HighScoreManager
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

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val highScoreManager = HighScoreManager(application)
    private val difficultyManager = DifficultyManager(application)

    private var orbSpeed = 8f
    private var spawnDelay = 1000L
    private var gameDuration = 60
    private var gameAreaWidth = 400f
    private var hitZoneTop = 430f
    private var hitZoneBottom = 570f
    private var missThreshold = 640f

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var nextOrbId = 0L

    init {
        applyDifficulty(difficultyManager.getDifficulty())
        _gameState.value = _gameState.value.copy(timeRemaining = gameDuration)
        startSpawning()
        startGameLoop()
        startTimer()
    }

    fun setGameAreaDimensions(widthDp: Float, heightDp: Float) {
        gameAreaWidth = widthDp
        hitZoneTop = heightDp - 200f
        hitZoneBottom = heightDp - 20f
        missThreshold = heightDp + 60f
    }

    private fun startSpawning() {
        viewModelScope.launch {
            while (true) {
                delay(spawnDelay)
                val state = _gameState.value
                if (state.isPaused || state.isGameOver || state.isLevelComplete) continue
                spawnOrb()
            }
        }
    }

    private fun spawnOrb() {
        val maxX = (gameAreaWidth - 50f).coerceAtLeast(0f)
        val newOrb = FallingOrb(
            id = nextOrbId++,
            positionX = Random.nextFloat() * maxX,
            color = OrbColor.entries.random(),
            positionY = 0f
        )
        _gameState.value = _gameState.value.copy(orbs = _gameState.value.orbs + newOrb)
    }

    private fun startGameLoop() {
        viewModelScope.launch {
            while (true) {
                delay(16L)
                val state = _gameState.value
                if (state.isPaused || state.isGameOver || state.isLevelComplete) continue
                updateOrbs()
            }
        }
    }

    private fun updateOrbs() {
        val movedOrbs = _gameState.value.orbs.map {
            it.copy(positionY = it.positionY + orbSpeed)
        }
        val missedOrbs = movedOrbs.filter { it.positionY > missThreshold }
        val remainingOrbs = movedOrbs.filter { it.positionY <= missThreshold }

        val currentLives = (_gameState.value.lives - missedOrbs.size).coerceAtLeast(0)
        val gameOver = currentLives <= 0

        if (gameOver) highScoreManager.saveScore(_gameState.value.score)

        _gameState.value = _gameState.value.copy(
            lives = currentLives,
            combo = if (missedOrbs.isNotEmpty()) 1 else _gameState.value.combo,
            isGameOver = gameOver,
            highScore = if (gameOver) highScoreManager.getHighScore() else _gameState.value.highScore,
            orbs = remainingOrbs
        )
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                val state = _gameState.value
                if (state.isPaused || state.isGameOver || state.isLevelComplete) continue

                val newTime = state.timeRemaining - 1
                if (newTime <= 0) {
                    highScoreManager.saveScore(state.score)
                    _gameState.value = state.copy(
                        timeRemaining = 0,
                        isLevelComplete = true,
                        highScore = highScoreManager.getHighScore()
                    )
                } else {
                    _gameState.value = state.copy(timeRemaining = newTime)
                }
            }
        }
    }

    fun hitColor(color: OrbColor) {
        val orb = _gameState.value.orbs.firstOrNull {
            it.color == color && it.positionY in hitZoneTop..hitZoneBottom
        }
        if (orb != null) {
            val pointsEarned = 10 + (_gameState.value.combo - 1) * 5
            val newScore = _gameState.value.score + pointsEarned
            val newCombo = _gameState.value.combo + 1
            _gameState.value = _gameState.value.copy(
                score = newScore,
                combo = newCombo,
                orbs = _gameState.value.orbs.filter { it.id != orb.id }
            )
        }
        // wrong color or bad timing: do nothing — only missed orbs cost lives
    }

    fun pauseGame() {
        _gameState.value = _gameState.value.copy(isPaused = !_gameState.value.isPaused)
    }

    fun restartGame() {
        nextOrbId = 0
        applyDifficulty(difficultyManager.getDifficulty())
        _gameState.value = GameState(timeRemaining = gameDuration)
    }

    private fun applyDifficulty(difficulty: DifficultyLevel) {
        when (difficulty) {
            DifficultyLevel.EASY -> { orbSpeed = 5f; spawnDelay = 1500L; gameDuration = 90 }
            DifficultyLevel.MEDIUM -> { orbSpeed = 8f; spawnDelay = 1000L; gameDuration = 60 }
            DifficultyLevel.HARD -> { orbSpeed = 12f; spawnDelay = 700L; gameDuration = 45 }
        }
    }
}

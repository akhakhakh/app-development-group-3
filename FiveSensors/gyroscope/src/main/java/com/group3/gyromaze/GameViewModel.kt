package com.group3.gyromaze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group3.gyromaze.model.GameState
import com.group3.gyromaze.model.TileType
import com.group3.gyromaze.model.Vec2
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

class GameViewModel : ViewModel() {
    // ----- Physics constants ------
    private val MARBLE_RADIUS = 0.38f
    private val BASE_FRICTION = 0.93f
    private val ICE_FRICTION = 0.988f
    private val TILT_FORCE = 0.032f
    private val DEADZONE = 0.04f
    private val FRAME_MS = 16L


    // ------ Level state ------
    private var curLvlIndex = 0
    val curLvl get() = MazeData.levels[curLvlIndex]

    private val _gameState = MutableStateFlow(GameState(marblePos = curLvl.marbleStart, marbleVelocity = Vec2(0f, 0f)))
    val gameState: StateFlow<GameState> = _gameState

    // ----- Door state -----
    private var doorTimer = 0f
    var doorsAreOpen = false
        private set

    // ------ Pause state -----
    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused

    private var latestTiltX = 0f
    private var latestTiltY = 0f

    init { startGameLoop() }

    fun updateTilt(tiltX: Float, tiltY: Float) {
        latestTiltX = if (abs(tiltX) < DEADZONE) 0f else tiltX
        latestTiltY = if (abs(tiltY) < DEADZONE) 0f else tiltY
    }

    fun togglePause() {
        _isPaused.value = !_isPaused.value
    }

    fun startTimer() {
        _gameState.value = _gameState.value.copy(isTimerRunning = true)
    }

    private fun startGameLoop() {
        viewModelScope.launch {
            while (true) {
                delay(FRAME_MS)
                tick()
            }
        }
    }

    private fun tick() {
        val state = _gameState.value
        // do nothing if paused, completed, game over or timer hasn't started
        if (_isPaused.value || state.isLvlComplete || state.isGameOver || !state.isTimerRunning) return

        val dt = FRAME_MS / 1000f

        // door toggle
        doorTimer += dt
        if (doorTimer >= curLvl.doorIntervalSeconds) {
            doorTimer = 0f
            doorsAreOpen = !doorsAreOpen
        }

        val friction = when (getTileAt(state.marblePos)) {
            TileType.ICED_FLOOR -> ICE_FRICTION
            else -> BASE_FRICTION
        }

        var velocity = Vec2(
            x = (state.marbleVelocity.x + latestTiltX * TILT_FORCE) * friction,
            y = (state.marbleVelocity.y + latestTiltY * TILT_FORCE) * friction
        )

        var position = state.marblePos

        val tryX = Vec2(position.x + velocity.x, position.y)
        if (!isWall(tryX)) {
            position = tryX
        }
        else {
            velocity = Vec2(-velocity.x * 0.2f, velocity.y)
        }

        val tryY = Vec2(position.x, position.y + velocity.y)
        if (!isWall(tryY)) {
            position = tryY
        }
        else {
            velocity = Vec2(velocity.x, -velocity.y * 0.2f)
        }

        var warped = checkTeleporter(position)
        while (warped != position) {
            position = warped
            warped = checkTeleporter(position)
        }

        val won = getTileAt(position) == TileType.GOAL

        _gameState.value = state.copy(
            marblePos = position,
            marbleVelocity = velocity,
            isLvlComplete = won,
            elapsedSeconds = state.elapsedSeconds + dt
        )
    }

    private fun isWall(pos: Vec2): Boolean {
        val corners = listOf(
            Vec2(pos.x - MARBLE_RADIUS, pos.y - MARBLE_RADIUS),
            Vec2(pos.x + MARBLE_RADIUS, pos.y - MARBLE_RADIUS),
            Vec2(pos.x - MARBLE_RADIUS, pos.y + MARBLE_RADIUS),
            Vec2(pos.x + MARBLE_RADIUS, pos.y + MARBLE_RADIUS)
        )
        return corners.any { c ->
            when (getTileAt(c)) {
                TileType.WALL -> true
                TileType.DOOR_CLOSED -> !doorsAreOpen
                else -> false
            }
        }
    }

    private fun getTileAt(pos: Vec2): TileType {
        val col = pos.x.toInt()
        val row = pos.y.toInt()
        if (col < 0 || col >= curLvl.cols || row < 0 || row >= curLvl.rows) return TileType.WALL

        return when (val tile = curLvl.grid[row][col]) {
            TileType.DOOR_CLOSED,
            TileType.DOOR_OPEN -> if (doorsAreOpen) TileType.DOOR_OPEN else TileType.DOOR_CLOSED
            else -> tile
        }
    }

    private fun checkTeleporter(pos: Vec2): Vec2 {
        val key = Vec2(pos.x.toInt().toFloat(), pos.y.toInt().toFloat())
        val exit = curLvl.teleporterPairs[key] ?: return pos

        return Vec2(exit.x + 0.5f, exit.y + 0.5f)
    }

    fun restartLvl() {
        doorTimer = 0f
        doorsAreOpen = false
        latestTiltX = 0f
        latestTiltY = 0f
        _isPaused.value = false
        _gameState.value = GameState(
            marblePos = curLvl.marbleStart,
            marbleVelocity = Vec2(0f, 0f),
            isTimerRunning = curLvl.tutorialMessage == null
        )
    }

    fun nextLvl() {
        if (curLvlIndex < MazeData.levels.size - 1) curLvlIndex++
        restartLvl()
    }

    val isLastLevel get() = curLvlIndex == MazeData.levels.lastIndex

    fun resetGame() {
        curLvlIndex = 0
        restartLvl()
    }
}
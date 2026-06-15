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
    private val MARBLE_RADIUS = 0.38f  // in grid units
    private val BASE_FRICTION = 0.88f  // multiplied against velocity each frame
    private val ICE_FRICTION = 0.975f // higher = more slippery (less slowdown)
    private val TILT_FORCE = 0.018f // acceleration per unit of tilt per frame
    private val DEADZONE = 0.04f // tilt values below this threshold are 0
    private val FRAME_MS = 16L // 16ms ≈ 60 FPS

    // ----- level State -----
    private var curLvlIndex = 0
    val curLvl get() = MazeData.levels[curLvlIndex]

    private val _gameState = MutableStateFlow(
        GameState(
            marblePos = curLvl.marbleStart,
            marbleVelocity = Vec2(0f, 0f)
        )
    )
    val gameState: StateFlow<GameState> = _gameState

    // ----- door state -----
    private var doorTimer = 0f
    var doorsAreOpen = false
        private set

    private var latestTiltX = 0f
    private var latestTiltY = 0f

    init { startGameLoop() }

    fun updateTilt(tiltX: Float, tiltY: Float) {
        latestTiltX = if (abs(tiltX) < DEADZONE) 0f else tiltX
        latestTiltY = if (abs(tiltY) < DEADZONE) 0f else tiltY
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
        if (state.isLvlComplete || state.isGameOver) return

        val dt = FRAME_MS / 1000f

        doorTimer += dt
        if (doorTimer >= curLvl.doorIntervalSeconds) {
            doorTimer = 0f
            doorsAreOpen = !doorsAreOpen
        }

        val friction = when (getTileAt(state.marblePos)) {
            TileType.ICED_FLOOR -> ICE_FRICTION
            else -> BASE_FRICTION
        }

        var vel = Vec2(
            x = (state.marbleVelocity.x + latestTiltX * TILT_FORCE) * friction,
            y = (state.marbleVelocity.y + latestTiltY * TILT_FORCE) * friction
        )

        var pos = state.marblePos

        val tryX = Vec2(pos.x + vel.x, pos.y)
        if (!isWall(tryX)) {
            pos = tryX
        } else {
            vel = Vec2(-vel.x * 0.2f, vel.y)
        }

        val tryY = Vec2(pos.x, pos.y + vel.y)
        if (!isWall(tryY)) {
            pos = tryY
        } else {
            vel = Vec2(vel.x, -vel.y * 0.2f)
        }

        // ----- teleporter ------
        pos = checkTeleporter(pos)

        // ----- goal check ------
        val won = getTileAt(pos) == TileType.GOAL

        _gameState.value = state.copy(
            marblePos = pos,
            marbleVelocity = vel,
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
        if (col < 0 || col >= curLvl.cols || row < 0 || row >= curLvl.rows) {
            return TileType.WALL
        }
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
        _gameState.value = GameState(
            marblePos = curLvl.marbleStart,
            marbleVelocity = Vec2(0f, 0f)
        )
    }

    fun nextLvl() {
        if (curLvlIndex < MazeData.levels.size - 1) curLvlIndex++
        restartLvl()
    }
}
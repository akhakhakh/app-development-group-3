package com.group3.gyromaze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group3.gyromaze.MazeData
import com.group3.gyromaze.model.GameState
import com.group3.gyromaze.model.TileType
import com.group3.gyromaze.model.Vec2
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    // configuration constants
    private val MARBLE_RADIUS = 0.4f   // in grid units
    private val BASE_FRICTION = 0.85f  // how fast the marble slows down on normal floor
    private val ICE_FRICTION  = 0.97f  // higher -> less slowdown -> more slippery
    private val TILT_FORCE    = 0.012f // how much each tilt unit accelerates the marble
    private val FRAME_DELAY_MS = 16L   // ~60 FPS (1000ms / 60 ≈ 16ms)

    // current level
    private var currentLevelIndex = 0
    val currentLevel get() = MazeData.levels[currentLevelIndex]

    // --- Game state as a Flow so Compose can observe it ---
    // StateFlow is like LiveData but works better with Kotlin coroutines
    private val _gameState = MutableStateFlow(
        GameState(marblePos = currentLevel.marbleStart, marbleVelocity = Vec2(0f, 0f))
    )
    val gameState: StateFlow<GameState> = _gameState

    // door timer
    private var doorTimer = 0f

    init {
        startGameLoop()
    }

    // the game loop runs on a coroutine -> it ticks every 16ms
    private fun startGameLoop() {
        viewModelScope.launch {
            while (true) {
                delay(FRAME_DELAY_MS)
                updatePhysics()
            }
        }
    }

    // called every frame with the current sensor tilt values
    fun updateTilt(tiltX: Float, tiltY: Float) {
        val state = _gameState.value
        if (state.isLevelComplete || state.isGameOver) return

        val dt = FRAME_DELAY_MS / 1000f  // Delta time in seconds

        // update door timer
        doorTimer += dt
        if (doorTimer >= currentLevel.doorIntervalSeconds) {
            doorTimer = 0f
            toggleDoors()
        }

        // apply tilt force to velocity (F = ma, but here mass = 1)
        val tile = getTileAt(state.marblePos)
        val friction = if (tile == TileType.ICED_FLOOR) ICE_FRICTION else BASE_FRICTION

        var newVelocity = Vec2(
            x = (state.marbleVelocity.x + tiltX * TILT_FORCE) * friction,
            y = (state.marbleVelocity.y + tiltY * TILT_FORCE) * friction
        )

        // try to move. check collisions on each axis separately
        // this is "axis-aligned collision resolution"
        var newPos = state.marblePos

        // Try X axis movement
        val posAfterX = Vec2(newPos.x + newVelocity.x, newPos.y)
        if (!isCollidingWithWall(posAfterX)) {
            newPos = posAfterX
        } else {
            newVelocity = newVelocity.copy(x = 0f) // Stop X movement on wall hit
        }

        // Try Y axis movement
        val posAfterY = Vec2(newPos.x, newPos.y + newVelocity.y)
        if (!isCollidingWithWall(posAfterY)) {
            newPos = posAfterY
        } else {
            newVelocity = newVelocity.copy(y = 0f) // Stop Y movement on wall hit
        }

        // Check teleporter
        newPos = checkTeleporter(newPos)

        // Check goal
        val isComplete = getTileAt(newPos) == TileType.GOAL

        _gameState.value = state.copy(
            marblePos = newPos,
            marbleVelocity = newVelocity,
            isLevelComplete = isComplete,
            elapsedSeconds = state.elapsedSeconds + dt
        )
    }

    // Manually trigger a physics tick (called from our game loop coroutine)
    private fun updatePhysics() {
        // The actual physics update happens in updateTilt(), called every frame
        // from the sensor reading. This function handles timer-only updates
        // (like timed doors) when the sensor hasn't changed.
    }

    private fun toggleDoors() {
        /*
        Doors are stored in the grid, I toggle between DOOR_OPEN and DOOR_CLOSED
        Since MazeLevel.grid is immutable, I track door state separately
        (simplified: doors toggle globally per level)
        */
        doorOpen = !doorOpen
    }

    private var doorOpen = false

    /*
    checks if a given position would overlap with a wall tile
    */
    private fun isCollidingWithWall(pos: Vec2): Boolean {
        // check the four corners of the marble's bounding box
        val offsets = listOf(
            Vec2(-MARBLE_RADIUS, -MARBLE_RADIUS),
            Vec2(+MARBLE_RADIUS, -MARBLE_RADIUS),
            Vec2(-MARBLE_RADIUS, +MARBLE_RADIUS),
            Vec2(+MARBLE_RADIUS, +MARBLE_RADIUS)
        )
        return offsets.any { offset ->
            val checkPos = pos + offset
            val tile = getTileAt(checkPos)
            tile == TileType.WALL ||
                    (tile == TileType.DOOR_CLOSED && !doorOpen)
        }
    }

    // gets the tile type at a given grid position
    private fun getTileAt(pos: Vec2): TileType {
        val col = pos.x.toInt()
        val row = pos.y.toInt()
        if (row < 0 || row >= currentLevel.rows || col < 0 || col >= currentLevel.cols) {
            return TileType.WALL // out of bounds = wall
        }
        return currentLevel.grid[row][col]
    }

    private fun checkTeleporter(pos: Vec2): Vec2 {
        val gridPos = Vec2(pos.x.toInt().toFloat(), pos.y.toInt().toFloat())
        return currentLevel.teleporterPairs[gridPos] ?: pos
    }

    fun restartLevel() {
        doorTimer = 0f
        doorOpen = false
        _gameState.value = GameState(
            marblePos = currentLevel.marbleStart,
            marbleVelocity = Vec2(0f, 0f)
        )
    }

    fun nextLevel() {
        if (currentLevelIndex < MazeData.levels.size - 1) {
            currentLevelIndex++
            restartLevel()
        }
    }
}
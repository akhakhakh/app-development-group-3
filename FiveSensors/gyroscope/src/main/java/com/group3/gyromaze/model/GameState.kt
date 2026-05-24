// Data classes
package com.group3.gyromaze.model

import androidx.compose.ui.unit.Velocity

// Types of tiles that the marble can interact with
enum class TileType {
    REG_FLOOR, // Normal floor -> normal friction
    WALL, // Solid wall -> the marble cannot go through
    GOAL, // The hole -> winning condition
    ICED_FLOOR, // Slippery floor -> reduced friction
    TELEPORTER, // Instantly moves the marble to another paired teleporter
    DOOR_OPEN, // Door opened -> marble can go through
    DOOR_CLOSED, // Door closed -> acts like a wall
}

// A 2D position in the game world (in grid cells, not pixels)
data class Vec2(val x : Float, val y: Float) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun times(scalar: Float) = Vec2(x * scalar, y * scalar)
}

// Full state of the game at any moment
data class GameState(
    val marblePos : Vec2, // Marble centre position (grid units)
    val marbleVelocity : Vec2, // Current speed and direction
    val isLevelComplete : Boolean = false,
    val isGameOver : Boolean = false,
    val elapsedSeconds : Float = 0f
)
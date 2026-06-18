// Data classes
package com.group3.gyromaze.model

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

// 2D position in grid units (fractional allowed for smooth movement)
data class Vec2(val x: Float, val y: Float) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun times(scalar: Float) = Vec2(x * scalar, y * scalar)
}

// the complete game state at any moment in time
data class GameState(
    val marblePos: Vec2,
    val marbleVelocity: Vec2,
    val isLvlComplete: Boolean = false,
    val isGameOver: Boolean = false,
    val elapsedSeconds: Float = 0f
)
package com.group3.touchscreen1p.model

data class GameState(
    val score: Int = 0,
    val combo: Int = 1,
    val lives: Int = 3,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false,
    val orbs: List<FallingOrb> = emptyList()
)
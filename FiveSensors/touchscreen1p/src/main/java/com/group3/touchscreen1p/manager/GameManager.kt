package com.group3.touchscreen1p.manager

import com.group3.touchscreen1p.model.GameState

object GameManager {

    fun calculateScore(
        gameState: GameState
    ): Int {

        return gameState.score +
                (10 * gameState.combo)
    }
}
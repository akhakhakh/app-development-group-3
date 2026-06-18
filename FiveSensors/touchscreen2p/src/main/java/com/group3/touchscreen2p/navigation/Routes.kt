package com.group3.touchscreen2p.navigation

object Routes {
    const val HOME = "home"
    const val GAME = "game"
    const val GAME_OVER = "game_over/{winner}/{score1}/{score2}/{bestCombo1}/{bestCombo2}"
    const val HOW_TO_PLAY = "how_to_play"
    const val SETTINGS    = "settings"

    fun gameOver(winner: Int, score1: Int, score2: Int, bestCombo1: Int, bestCombo2: Int) = "game_over/$winner/$score1/$score2/$bestCombo1/$bestCombo2"
}
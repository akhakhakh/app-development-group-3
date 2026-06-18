package com.group3.touchscreen1p.navigation

sealed class Routes(val route: String) {

    data object Home : Routes("home")

    data object Game : Routes("game")

    data object Settings : Routes("settings")

    data object HighScore : Routes("highscore")

    data object HowToPlay : Routes("howtoplay")

    data object GameOver : Routes("game_over/{score}/{highScore}")
}
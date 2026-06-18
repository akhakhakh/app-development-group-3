package com.group3.touchscreen1p.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.group3.touchscreen1p.manager.HighScoreManager
import com.group3.touchscreen1p.ui.*

@Composable
fun NavGraph() {

    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {

        composable(Routes.Home.route) {
            HomeScreen(navController)
        }

        composable(Routes.Game.route) {
            GameScreen()
        }

        composable(Routes.Settings.route) {
            SettingsScreen(navController)
        }

        composable(Routes.HighScore.route) {
            HighScoreScreen(highScore = HighScoreManager.getHighScore(context))
        }

        composable(Routes.HowToPlay.route) {
            HowToPlayScreen()
        }
    }
}
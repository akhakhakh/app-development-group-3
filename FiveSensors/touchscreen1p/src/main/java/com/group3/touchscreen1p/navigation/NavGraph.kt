package com.group3.touchscreen1p.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.group3.touchscreen1p.ui.*

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {

        composable(Routes.Home.route) {
            HomeScreen(navController)
        }

        composable(Routes.Game.route) {
            GameScreen(navController)
        }

        composable(Routes.Settings.route) {
            SettingsScreen(navController)
        }

        composable(Routes.HighScore.route) {
            HighScoreScreen(navController)
        }

        composable(Routes.HowToPlay.route) {
            HowToPlayScreen(navController)
        }
    }
}
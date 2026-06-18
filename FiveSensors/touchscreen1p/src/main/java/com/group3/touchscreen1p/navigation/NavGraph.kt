package com.group3.touchscreen1p.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
            HighScoreScreen(navController = navController)
        }

        composable(Routes.HowToPlay.route) {
            HowToPlayScreen(navController = navController)
        }

        composable(
            route = Routes.GameOver.route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("highScore") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val highScore = backStackEntry.arguments?.getInt("highScore") ?: 0
            GameOverScreen(
                score = score,
                highScore = highScore,
                navController = navController,
                onRetry = {
                    navController.navigate(Routes.Game.route) {
                        popUpTo(Routes.Home.route)
                    }
                }
            )
        }
    }
}

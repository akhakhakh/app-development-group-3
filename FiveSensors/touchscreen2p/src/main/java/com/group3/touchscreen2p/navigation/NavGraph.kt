package com.group3.touchscreen2p.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.group3.touchscreen2p.ui.GameOverScreen
import com.group3.touchscreen2p.ui.GameScreen
import com.group3.touchscreen2p.ui.HomeScreen
import com.group3.touchscreen2p.ui.HowToPlayScreen
import com.group3.touchscreen2p.viewmodel.GameViewModel

@Composable
fun TapBattleNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onPlayClick = { navController.navigate(Routes.GAME) },
                onHowToPlayClick = { navController.navigate(Routes.HOW_TO_PLAY) }
            )
        }

        composable(Routes.HOW_TO_PLAY) {
            HowToPlayScreen(onBack = { navController.popBackStack() })
        }


        composable(Routes.GAME) {
            val viewModel: GameViewModel = viewModel()
            GameScreen(
                viewModel = viewModel,
                onGameOver = { winner, score1, score2, bestCombo ->
                    navController.navigate(Routes.gameOver(winner, score1, score2, bestCombo)) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                },
                onHome = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }

        composable(
            route = Routes.GAME_OVER,
            arguments = listOf(
                navArgument("winner") { type = NavType.IntType },
                navArgument("score1") { type = NavType.IntType },
                navArgument("score2") { type = NavType.IntType },
                navArgument("bestCombo") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val winner = backStackEntry.arguments?.getInt("winner") ?: 0
            val score1 = backStackEntry.arguments?.getInt("score1") ?: 0
            val score2 = backStackEntry.arguments?.getInt("score2") ?: 0

            GameOverScreen(
                winner = winner,
                score1 = score1,
                score2 = score2,
                onPlayAgain = {
                    navController.navigate(Routes.GAME) {
                        popUpTo(Routes.HOME)
                    }
                },
                onHome = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                }
            )
        }
    }
}
package com.group3.touchscreen2p.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.group3.touchscreen2p.ui.GameScreen
import com.group3.touchscreen2p.ui.HomeScreen
import com.group3.touchscreen2p.ui.theme.NavyBackground
import com.group3.touchscreen2p.ui.theme.YellowAccent
import com.group3.touchscreen2p.viewmodel.GameViewModel

@Composable
fun TapBattleNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(onPlayClick = { navController.navigate(Routes.GAME) })
        }

        composable(Routes.GAME) {
            val viewModel: GameViewModel = viewModel()
            GameScreen(
                viewModel = viewModel,
                onGameOver = { winner, score1, score2, bestCombo ->
                    navController.navigate(Routes.gameOver(winner, score1, score2, bestCombo)) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.GAME_OVER,
            arguments = listOf(
                navArgument("winner")    { type = NavType.IntType },
                navArgument("score1")    { type = NavType.IntType },
                navArgument("score2")    { type = NavType.IntType },
                navArgument("bestCombo") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val winner    = backStackEntry.arguments?.getInt("winner")    ?: 0
            val score1    = backStackEntry.arguments?.getInt("score1")    ?: 0
            val score2    = backStackEntry.arguments?.getInt("score2")    ?: 0

            Box(
                modifier = Modifier.fillMaxSize().background(NavyBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PLAYER $winner WINS!\n$score1 - $score2",
                    color = YellowAccent,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

    }

}
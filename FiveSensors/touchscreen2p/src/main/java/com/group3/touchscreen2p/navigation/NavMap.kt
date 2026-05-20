package com.group3.touchscreen2p.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun TapBattleNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
        }

        composable(Routes.GAME) {
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

        }

    }

}
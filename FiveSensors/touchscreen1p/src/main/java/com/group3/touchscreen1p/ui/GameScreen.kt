package com.group3.touchscreen1p.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.group3.touchscreen1p.ui.theme.BackgroundDark
import com.group3.touchscreen1p.ui.theme.NeonReactorTheme

@Composable
fun GameScreen(navController: NavHostController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark),
        contentAlignment = Alignment.Center
    ) {
        Text("GAME SCREEN")
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    NeonReactorTheme {
        GameScreen(
            navController = rememberNavController()
        )
    }
}
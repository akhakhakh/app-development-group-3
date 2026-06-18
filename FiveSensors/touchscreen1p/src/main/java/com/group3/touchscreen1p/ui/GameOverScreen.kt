package com.group3.touchscreen1p.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameOverScreen(
    score: Int,
    highScore: Int,
    onRetry: () -> Unit,
    onMenu: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("GAME OVER")

        Spacer(Modifier.height(16.dp))

        Text("Score: $score")

        Text("Best: $highScore")

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onRetry
        ) {
            Text("Retry")
        }

        Button(
            onClick = onMenu
        ) {
            Text("Menu")
        }
    }
}
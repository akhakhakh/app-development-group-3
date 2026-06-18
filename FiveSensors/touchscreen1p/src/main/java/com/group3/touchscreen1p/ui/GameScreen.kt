package com.group3.touchscreen1p.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.group3.touchscreen1p.R
import com.group3.touchscreen1p.model.OrbColor
import com.group3.touchscreen1p.navigation.Routes
import com.group3.touchscreen1p.ui.components.ColorButton
import com.group3.touchscreen1p.ui.components.OrbView
import com.group3.touchscreen1p.ui.theme.BackgroundDark
import com.group3.touchscreen1p.viewmodel.GameViewModel

@Composable
fun GameScreen(
    navController: NavController,
    viewModel: GameViewModel = viewModel()
) {

    val gameState by viewModel.gameState.collectAsStateWithLifecycle()

    LaunchedEffect(gameState.isGameOver, gameState.isLevelComplete) {
        if (gameState.isGameOver || gameState.isLevelComplete) {
            navController.navigate("game_over/${gameState.score}/${gameState.highScore}") {
                popUpTo(Routes.Game.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .navigationBarsPadding()
        ) {

            // TOP HUD
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(text = "Score: ${gameState.score}", color = Color.Cyan)
                    Text(text = "Combo x${gameState.combo}", color = Color.Yellow)
                }

                Text(
                    text = "${gameState.timeRemaining}s",
                    color = if (gameState.timeRemaining <= 10) Color.Red else Color.White
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(3) { index ->
                        Image(
                            painter = painterResource(
                                id = if (index < gameState.lives)
                                    R.drawable.heart_icon_pink
                                else
                                    R.drawable.heart_icon_grey
                            ),
                            contentDescription = "Life",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Image(
                        painter = painterResource(R.drawable.pause_button),
                        contentDescription = "Pause",
                        modifier = Modifier
                            .size(42.dp)
                            .clickable { viewModel.pauseGame() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // GAMEPLAY AREA
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val widthDp = maxWidth.value
                val heightDp = maxHeight.value

                LaunchedEffect(widthDp, heightDp) {
                    viewModel.setGameAreaDimensions(widthDp, heightDp)
                }

                // Target zone at bottom
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(Color.Magenta.copy(alpha = 0.12f))
                )

                // Falling orbs — positionX is already in dp within the game area
                gameState.orbs.forEach { orb ->
                    OrbView(
                        color = orb.color,
                        modifier = Modifier
                            .offset(x = orb.positionX.dp, y = orb.positionY.dp)
                            .size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // COLOR BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ColorButton(drawable = R.drawable.squre_blue) { viewModel.hitColor(OrbColor.CYAN) }
                ColorButton(drawable = R.drawable.squre_pink) { viewModel.hitColor(OrbColor.PINK) }
                ColorButton(drawable = R.drawable.squre_yellow) { viewModel.hitColor(OrbColor.YELLOW) }
                ColorButton(drawable = R.drawable.squre_purple) { viewModel.hitColor(OrbColor.PURPLE) }
            }
        }

        // PAUSE DIALOG
        if (gameState.isPaused) {
            PauseDialog(
                onResume = { viewModel.pauseGame() },
                onRestart = { viewModel.restartGame() },
                onExit = { navController.popBackStack() }
            )
        }
    }
}

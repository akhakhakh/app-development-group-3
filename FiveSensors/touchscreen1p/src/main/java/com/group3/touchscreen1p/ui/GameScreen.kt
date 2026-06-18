package com.group3.touchscreen1p.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    if (gameState.isGameOver) {

        navController.navigate("game_over") {
            popUpTo("game") {
                inclusive = true
            }
        }

        return
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
        ) {

            // =========================
            // TOP HUD
            // =========================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Score: ${gameState.score}",
                        color = Color.Cyan
                    )

                    Text(
                        text = "Combo x${gameState.combo}",
                        color = Color.Yellow
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    repeat(3) { index ->

                        Image(
                            painter = painterResource(
                                id =
                                    if (index < gameState.lives)
                                        R.drawable.heart_icon_pink
                                    else
                                        R.drawable.heart_icon_grey
                            ),
                            contentDescription = "Life",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Image(
                        painter = painterResource(
                            R.drawable.pause_button
                        ),
                        contentDescription = "Pause",
                        modifier = Modifier
                            .size(42.dp)
                            .clickable {
                                viewModel.pauseGame()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // =========================
            // GAMEPLAY AREA
            // =========================

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                // Lane lines
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {

                    repeat(4) { lane ->

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )

                        if (lane < 3) {

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(2.dp)
                                    .background(
                                        Color.White.copy(
                                            alpha = 0.15f
                                        )
                                    )
                            )
                        }
                    }
                }

                // Target zone
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Color.Magenta.copy(
                                alpha = 0.12f
                            )
                        )
                )

                // Falling Orbs
                gameState.orbs.forEach { orb ->

                    OrbView(
                        color = orb.color,
                        modifier = Modifier
                            .offset(
                                x = (orb.lane * 95).dp,
                                y = orb.positionY.dp
                            )
                            .size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // =========================
            // COLOR BUTTONS
            // =========================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                ColorButton(
                    drawable = R.drawable.squre_blue
                ) {
                    viewModel.hitColor(
                        OrbColor.CYAN,
                        0
                    )
                }

                ColorButton(
                    drawable = R.drawable.squre_pink
                ) {
                    viewModel.hitColor(
                        OrbColor.PINK,
                        1
                    )
                }

                ColorButton(
                    drawable = R.drawable.squre_yellow
                ) {
                    viewModel.hitColor(
                        OrbColor.YELLOW,
                        2
                    )
                }

                ColorButton(
                    drawable = R.drawable.squre_purple
                ) {
                    viewModel.hitColor(
                        OrbColor.PURPLE,
                        3
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // =========================
        // PAUSE DIALOG
        // =========================

        if (gameState.isPaused) {

            PauseDialog(

                onResume = {
                    viewModel.pauseGame()
                },

                onRestart = {
                    viewModel.restartGame()
                },

                onExit = {
                    navController.popBackStack()
                }
            )
        }
    }
}
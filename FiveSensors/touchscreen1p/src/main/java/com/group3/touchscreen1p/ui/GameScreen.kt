package com.group3.touchscreen1p.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Button
import com.group3.touchscreen1p.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group3.touchscreen1p.model.OrbColor
import com.group3.touchscreen1p.ui.theme.BackgroundDark
import com.group3.touchscreen1p.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {

    val gameState by viewModel.gameState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {

        // HUD
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = "Score: ${gameState.score}",
                    color = Color.White
                )

                Text(
                    text = "Combo x${gameState.combo}",
                    color = Color.Yellow
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
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
            }
        }

        // Gameplay Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            gameState.orbs.forEach { orb ->

                Box(
                    modifier = Modifier
                        .padding(
                            start = (orb.lane * 90).dp,
                            top = orb.positionY.dp
                        )
                        .size(50.dp)
                        .background(
                            orb.color.color,
                            CircleShape
                        )
                )
            }
        }

        // Color Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            OrbColor.entries.forEach { color ->

                Button(
                    onClick = {
                        viewModel.hitColor(color)
                    },
                    modifier = Modifier.height(60.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color.color,
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}
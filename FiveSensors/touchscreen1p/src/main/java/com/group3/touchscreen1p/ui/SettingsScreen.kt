package com.group3.touchscreen1p.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.group3.touchscreen1p.R
import com.group3.touchscreen1p.model.DifficultyLevel
import com.group3.touchscreen1p.ui.theme.BackgroundDark

@Composable
fun SettingsScreen(
    navController: NavController
) {

    var soundEnabled by remember { mutableStateOf(true) }
    var musicEnabled by remember { mutableStateOf(true) }

    var difficulty by remember {
        mutableStateOf(DifficultyLevel.MEDIUM)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Image(
                    painter = painterResource(R.drawable.back_menu_button),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Settings",
                color = Color.Cyan,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A0A2E)
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    "Audio",
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row {
                        Image(
                            painter = painterResource(R.drawable.sound_icon),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            "Sound Effects",
                            color = Color.White
                        )
                    }

                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row {
                        Image(
                            painter = painterResource(R.drawable.music_icon),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            "Music",
                            color = Color.White
                        )
                    }

                    Switch(
                        checked = musicEnabled,
                        onCheckedChange = {
                            musicEnabled = it
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A0A2E)
            )
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    "Difficulty",
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    DifficultyButton(
                        text = "Easy",
                        selected = difficulty == DifficultyLevel.EASY
                    ) {
                        difficulty = DifficultyLevel.EASY
                    }

                    DifficultyButton(
                        text = "Medium",
                        selected = difficulty == DifficultyLevel.MEDIUM
                    ) {
                        difficulty = DifficultyLevel.MEDIUM
                    }

                    DifficultyButton(
                        text = "Hard",
                        selected = difficulty == DifficultyLevel.HARD
                    ) {
                        difficulty = DifficultyLevel.HARD
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor =
                if (selected)
                    Color.Magenta
                else
                    Color.DarkGray
        )
    ) {
        Text(text)
    }
}
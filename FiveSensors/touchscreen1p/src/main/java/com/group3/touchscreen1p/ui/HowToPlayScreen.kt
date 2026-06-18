package com.group3.touchscreen1p.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.group3.touchscreen1p.R
import com.group3.touchscreen1p.ui.theme.BackgroundDark

@Composable
fun HowToPlayScreen(
    navController: NavController
) {

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
                onClick = {
                    navController.popBackStack()
                }
            ) {

                Image(
                    painter = painterResource(
                        R.drawable.back_menu_button
                    ),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "How To Play",
                color = Color.Cyan,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        InstructionCard(
            title = "Objective",
            text = "Match falling orbs with the correct color button."
        )

        InstructionCard(
            title = "Scoring",
            text = "Every hit gives 10 points. Combos multiply your score."
        )

        InstructionCard(
            title = "Lives",
            text = "You have 3 lives. Missing orbs removes lives."
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Color Zones",
            color = Color.Cyan
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            ColorCircle(Color.Cyan)
            ColorCircle(Color.Magenta)
            ColorCircle(Color.Yellow)
            ColorCircle(Color(0xFF9D00FF))
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Got It!")
        }
    }
}

@Composable
private fun InstructionCard(
    title: String,
    text: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(title)

            Spacer(modifier = Modifier.height(4.dp))

            Text(text)
        }
    }
}

@Composable
private fun ColorCircle(
    color: Color
) {

    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                color,
                CircleShape
            )
    )
}
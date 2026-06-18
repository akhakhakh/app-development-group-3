package com.group3.touchscreen1p.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.group3.touchscreen1p.R
import com.group3.touchscreen1p.manager.HighScoreManager
import com.group3.touchscreen1p.ui.theme.BackgroundDark

@Composable
fun HighScoreScreen(
    navController: NavController
) {

    val context = LocalContext.current

    val highScore =
        remember {
            HighScoreManager(context)
                .getHighScore()
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(
                R.drawable.back_menu_button
            ),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Start)
                .clickable {
                    navController.popBackStack()
                }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(
                R.drawable.cup_highest_score_icon
            ),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "HIGH SCORE",
            color = Color.Cyan,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = highScore.toString(),
            color = Color.White,
            style = MaterialTheme.typography.displayLarge
        )
    }
}
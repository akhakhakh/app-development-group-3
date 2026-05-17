package com.group3.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LandingBlue = Color(0xFF3885F0)
private val LandingBackground = Color(0xFFFAFAFA)

@Composable
fun LandingScreen(onPlay: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LandingBackground)
    ) {
        // App bar — matches blue header bar in Figma
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(LandingBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "FACE SNAP",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Body: emoticon centred, PLAY button near the bottom
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EmoticonFace(
                expression = EmoticonExpression.NEUTRAL,
                modifier = Modifier.size(160.dp)
            )

            Spacer(Modifier.height(120.dp))

            Button(
                onClick = onPlay,
                modifier = Modifier
                    .width(240.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LandingBlue)
            ) {
                Text(
                    text = "PLAY",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

package com.group3.gyromaze

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group3.gyromaze.ui.theme.*

@Composable
fun HowToPlayScreen(
    onPlay: () -> Unit,
    onReturn: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep)
    ) {
        // Back arrow
        Text(
            text = "←",
            fontSize = 24.sp,
            color = TextWhite,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.TopStart)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(24.dp))
                .background(NavyCard)
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(listOf(NeonCyan, NeonPurple)),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 28.dp, vertical = 32.dp)
        ) {
            Text(
                text = "How to Play",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(Modifier.height(20.dp))

            // Phone tilt illustration (emoji stand-in)
            Text("📱", fontSize = 64.sp)

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Tilt your phone left, right, up, or down\nto move the marble around the board.",
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "The main objective is to put the\nmarble inside the hole.",
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Do you think you can complete the game?",
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(28.dp))

            WireframeButton(label = "Play", onClick = onPlay)
            Spacer(Modifier.height(12.dp))
            WireframeButton(label = "Return", onClick = onReturn)
        }
    }
}
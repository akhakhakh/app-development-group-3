package com.group3.gyromaze

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.group3.gyroscope.R
import com.group3.gyromaze.ui.theme.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

@Composable
fun MainMenuScreen(
    onPlay: () -> Unit,
    onHowToPlay: () -> Unit,
    onReturn: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyDeep),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .clip(RoundedCornerShape(24.dp))
                .background(NavyCard)
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(listOf(NeonCyan, NeonPurple)),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 28.dp, vertical = 36.dp)
        ) {
            // Logo placeholder — square icon outlined in neon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(listOf(NeonCyan, NeonPurple)),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(NavyMid),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Title
            Text(
                text = "GYRO MAZE",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NeonPurple,
                letterSpacing = 4.sp
            )

            // Divider
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, NeonPurple, Color.Transparent)
                        )
                    )
            )
            Spacer(Modifier.height(24.dp))

            // Buttons
            WireframeButton(label = "Play Game", onClick = onPlay)
            Spacer(Modifier.height(12.dp))
            WireframeButton(label = "Instructions", onClick = onHowToPlay)
            Spacer(Modifier.height(12.dp))
            WireframeButton(label = "Return", onClick = onReturn)

            Spacer(Modifier.height(28.dp))

            // Tagline
            Text(
                text = "Can you solve these puzzles\nand reach the goal?",
                fontSize = 13.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
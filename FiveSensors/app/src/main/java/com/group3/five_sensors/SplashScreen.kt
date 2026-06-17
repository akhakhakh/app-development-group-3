package com.group3.five_sensors

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgDark = Color(0xFF08091A)
private val Cyan = Color(0xFF4DD9FF)
private val CyanDim = Color(0xFF2A7A99)

@Composable
fun SplashScreen(onStart: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val blinkAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                1f at 0
                1f at 700
                0f at 850
                0f at 1050
                1f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onStart() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "5  SENSORS",
                color = Cyan,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "5 SENSOR-POWERED GAMES",
                color = CyanDim,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(72.dp))

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                color = Cyan.copy(alpha = blinkAlpha),
                thickness = 1.5.dp
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "TAP TO START",
                color = Cyan.copy(alpha = blinkAlpha),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                color = Cyan.copy(alpha = blinkAlpha),
                thickness = 1.5.dp
            )
        }

        Text(
            text = "v1.0",
            color = CyanDim.copy(alpha = 0.6f),
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
        )
    }
}

package com.group3.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

private val LandingBlue = Color(0xFF3885F0)
private val LandingBackground = Color(0xFFFAFAFA)

private val emoticonDrawables = listOf(
    R.drawable.emoticon_neutral,
    R.drawable.smile,
    R.drawable.neutral_leftwink,
    R.drawable.smile_leftwink,
    R.drawable.neutral_rightwink,
    R.drawable.smile_rightwink,
    R.drawable.neutral_closeeyes,
    R.drawable.smile_closeeyes,
    R.drawable.neutral_lookleft,
    R.drawable.smile_lookleft,
    R.drawable.neutral_lookright,
    R.drawable.smile_lookright,
    R.drawable.neutral_lookup,
    R.drawable.smile_lookup,
    R.drawable.neutral_lookdown,
    R.drawable.smile_lookdown,
    R.drawable.tiltleft,
    R.drawable.tiltright,
)

@Composable
fun LandingScreen(onPlay: () -> Unit, onHowToPlay: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LandingBackground)
            .statusBarsPadding()
    ) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EmoticonParade(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(Modifier.height(100.dp))

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

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onHowToPlay,
                modifier = Modifier
                    .width(240.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = LandingBlue
                )
            ) {
                Text(
                    text = "HOW TO PLAY",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmoticonParade(modifier: Modifier = Modifier) {
    val spawnIntervalMs = 1800L
    val travelDurationMs = 9000L
    val travelFrom =  1.4f
    val travelTo   = -1.4f

    var elapsedMs by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        val start = withFrameMillis { it }
        while (true) {
            withFrameMillis { elapsedMs = it - start }
        }
    }

    val sequence = remember {
        val result = mutableListOf<Int>()
        val pool = ArrayDeque(emoticonDrawables.shuffled())
        repeat(80) {
            if (pool.isEmpty()) pool.addAll(emoticonDrawables.shuffled())
            var pick = pool.removeFirst()
            if (pick == result.lastOrNull() && pool.isNotEmpty()) {
                pool.addLast(pick)
                pick = pool.removeFirst()
            }
            result.add(pick)
        }
        result
    }

    BoxWithConstraints(
        modifier = modifier.clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        val containerWidthPx = with(LocalDensity.current) { maxWidth.toPx() }

        val currentSlot = (elapsedMs / spawnIntervalMs).toInt()
        val lookBack = (travelDurationMs / spawnIntervalMs + 2).toInt()

        for (slot in (currentSlot - lookBack)..currentSlot) {
            val age = elapsedMs - slot * spawnIntervalMs
            if (age < 0 || age > travelDurationMs) continue

            val progress = age.toFloat() / travelDurationMs
            val x = travelFrom + progress * (travelTo - travelFrom)
            val dist = abs(x)

            val alpha = (1f - dist * 1.4f).coerceIn(0f, 1f)
            val scale = (1f - dist * 0.38f).coerceIn(0.35f, 1f)

            val drawableId = sequence[((slot % sequence.size) + sequence.size) % sequence.size]

            Image(
                painter = painterResource(id = drawableId),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        translationX = x * containerWidthPx
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
            )
        }
    }
}

package com.group3.five_sensors

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group3.five_sensors.ui.theme.FiveSensorsTheme

private val BgBlue = Color(0xFF4DA6D4)
private val Yellow = Color(0xFFFFD426)
private val CharDark = Color(0xFF1A2035)

@Composable
fun HomeScreen(
    onPlay: () -> Unit = {},
    onHowToPlay: () -> Unit = {},
    onSettings: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBlue)
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(72.dp))

        Text("VOICE", color = Color.White, fontSize = 52.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 52.sp)
        Text("JUMP", color = Yellow, fontSize = 52.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 52.sp)

        Spacer(Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center) {
            SoundWaveLeft()
            Spacer(Modifier.width(8.dp))
            NinjaCharacter()
            Spacer(Modifier.width(8.dp))
            SoundWaveRight()
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "SCREAM TO JUMP HIGHER!",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(52.dp))

        Button(
            onClick = onPlay,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Yellow, contentColor = CharDark)
        ) {
            Text("PLAY", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }

        Spacer(Modifier.height(14.dp))

        OutlinedButton(
            onClick = onHowToPlay,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            border = BorderStroke(2.dp, Color.White),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text("HOW TO PLAY", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(14.dp))

        OutlinedButton(
            onClick = onSettings,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            border = BorderStroke(2.dp, Color.White),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text("SETTINGS", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NinjaCharacter() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(width = 38.dp, height = 30.dp)
                .background(CharDark, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(7.dp)
                    .background(Color(0xFF2E3F5C), RoundedCornerShape(3.dp))
            )
        }
        Box(
            modifier = Modifier
                .size(width = 54.dp, height = 62.dp)
                .background(CharDark, RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(9.dp)
                .background(Yellow, RoundedCornerShape(4.dp))
        )
    }
}

@Composable
private fun SoundWaveLeft() {
    Canvas(modifier = Modifier.size(width = 36.dp, height = 70.dp)) {
        val stroke = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        val waveColor = Color.White.copy(alpha = 0.85f)
        drawArc(
            color = waveColor, startAngle = 120f, sweepAngle = 120f, useCenter = false,
            style = stroke, topLeft = Offset(size.width * 0.3f, size.height * 0.15f),
            size = Size(size.width * 0.8f, size.height * 0.7f)
        )
        drawArc(
            color = waveColor, startAngle = 130f, sweepAngle = 100f, useCenter = false,
            style = stroke, topLeft = Offset(size.width * -0.1f, size.height * 0.05f),
            size = Size(size.width * 1.1f, size.height * 0.9f)
        )
    }
}

@Composable
private fun SoundWaveRight() {
    Canvas(modifier = Modifier.size(width = 36.dp, height = 70.dp)) {
        val stroke = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        val waveColor = Color.White.copy(alpha = 0.85f)
        drawArc(
            color = waveColor, startAngle = -60f, sweepAngle = 120f, useCenter = false,
            style = stroke, topLeft = Offset(size.width * -0.1f, size.height * 0.15f),
            size = Size(size.width * 0.8f, size.height * 0.7f)
        )
        drawArc(
            color = waveColor, startAngle = -50f, sweepAngle = 100f, useCenter = false,
            style = stroke, topLeft = Offset(size.width * -0.05f, size.height * 0.05f),
            size = Size(size.width * 1.1f, size.height * 0.9f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FiveSensorsTheme {
        HomeScreen()
    }
}

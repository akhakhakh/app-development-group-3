package com.group3.gyromaze

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group3.gyromaze.model.TileType
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    sensorHandler: SensorHandler,
    onLevelComplete: (score: Int) -> Unit,
    onLevelFailed: (score: Int) -> Unit,
    onReturn: () -> Unit
) {
    val state    by viewModel.gameState.collectAsState()
    val level     = viewModel.curLvl
    val doorsOpen = viewModel.doorsAreOpen

    // Fire callbacks when state changes
    LaunchedEffect(state.isLvlComplete) {
        if (state.isLvlComplete) {
            val score = calculateScore(state.elapsedSeconds)
            onLevelComplete(score)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.updateTilt(sensorHandler.tiltX, sensorHandler.tiltY)
            delay(16)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1021))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val tileSize = minOf(size.width / level.cols, size.height / level.rows)
            val offsetX  = (size.width  - tileSize * level.cols) / 2f
            val offsetY  = (size.height - tileSize * level.rows) / 2f

            for (row in 0 until level.rows) {
                for (col in 0 until level.cols) {
                    val rawTile = level.grid[row][col]
                    val tile = when (rawTile) {
                        TileType.DOOR_CLOSED, TileType.DOOR_OPEN ->
                            if (doorsOpen) TileType.DOOR_OPEN else TileType.DOOR_CLOSED
                        else -> rawTile
                    }
                    val left     = offsetX + col * tileSize
                    val top      = offsetY + row * tileSize
                    val tileRect = Size(tileSize, tileSize)

                    when (tile) {
                        TileType.WALL -> {
                            drawRect(color = Color(0xFF1C2333), topLeft = Offset(left, top), size = tileRect)
                            drawRect(color = Color(0xFF2D3A50), topLeft = Offset(left + 2f, top + 2f), size = Size(tileSize - 4f, tileSize - 4f))
                            drawRect(color = Color(0xFF0D1117), topLeft = Offset(left, top), size = tileRect, style = Stroke(width = 1f))
                        }
                        TileType.GOAL -> {
                            drawRect(color = Color(0xFF1A1F3A), topLeft = Offset(left, top), size = tileRect)
                            val cx = left + tileSize / 2f
                            val cy = top  + tileSize / 2f
                            val holeRadius = tileSize * 0.38f
                            drawCircle(color = Color(0x99000000), radius = holeRadius * 1.25f, center = Offset(cx, cy))
                            drawCircle(
                                brush = Brush.radialGradient(listOf(Color(0xFF000000), Color(0xFF0A0A0A), Color(0xFF222222)), center = Offset(cx, cy), radius = holeRadius),
                                radius = holeRadius, center = Offset(cx, cy)
                            )
                            drawCircle(color = Color(0x55FFFFFF), radius = holeRadius, center = Offset(cx, cy), style = Stroke(width = 1.5f))
                        }
                        TileType.ICED_FLOOR -> {
                            drawRect(color = Color(0xFF1A2A4A), topLeft = Offset(left, top), size = tileRect)
                            drawLine(color = Color(0x8800E5FF), start = Offset(left + tileSize * 0.2f, top + tileSize * 0.1f), end = Offset(left + tileSize * 0.5f, top + tileSize * 0.4f), strokeWidth = tileSize * 0.06f)
                            drawRect(color = Color(0x2200E5FF), topLeft = Offset(left, top), size = tileRect, style = Stroke(width = 0.5f))
                        }
                        TileType.TELEPORTER -> {
                            drawRect(color = Color(0xFF1A0033), topLeft = Offset(left, top), size = tileRect)
                            val cx = left + tileSize / 2f
                            val cy = top  + tileSize / 2f
                            drawCircle(
                                brush = Brush.radialGradient(listOf(Color(0xFFBB86FC), Color(0x00BB86FC)), center = Offset(cx, cy), radius = tileSize * 0.45f),
                                radius = tileSize * 0.45f, center = Offset(cx, cy)
                            )
                        }
                        TileType.DOOR_OPEN -> {
                            drawRect(color = Color(0xFF1A1F3A), topLeft = Offset(left, top), size = tileRect)
                            drawRect(color = Color(0x3300E5FF), topLeft = Offset(left, top), size = tileRect)
                        }
                        TileType.DOOR_CLOSED -> {
                            drawRect(color = Color(0xFF3A0A1A), topLeft = Offset(left, top), size = tileRect)
                            val cx = left + tileSize / 2f; val cy = top + tileSize / 2f; val arm = tileSize * 0.25f
                            drawLine(color = Color(0xFFFF4081), start = Offset(cx - arm, cy - arm), end = Offset(cx + arm, cy + arm), strokeWidth = tileSize * 0.1f)
                            drawLine(color = Color(0xFFFF4081), start = Offset(cx + arm, cy - arm), end = Offset(cx - arm, cy + arm), strokeWidth = tileSize * 0.1f)
                        }
                        else -> {
                            drawRect(color = Color(0xFF1A1F3A), topLeft = Offset(left, top), size = tileRect)
                            drawRect(color = Color(0x18FFFFFF), topLeft = Offset(left, top), size = tileRect, style = Stroke(width = 0.5f))
                        }
                    }
                }
            }

            // Marble
            val marblePx = offsetX + state.marblePos.x * tileSize
            val marblePy = offsetY + state.marblePos.y * tileSize
            val r = tileSize * 0.38f
            drawCircle(color = Color(0x55000000), radius = r * 0.85f, center = Offset(marblePx + r * 0.12f, marblePy + r * 0.18f))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE0E0E0), Color(0xFFA0A0A0), Color(0xFF404040)),
                    center = Offset(marblePx - r * 0.3f, marblePy - r * 0.3f),
                    radius = r * 1.4f
                ),
                radius = r, center = Offset(marblePx, marblePy)
            )
            drawCircle(color = Color.White.copy(alpha = 0.9f), radius = r * 0.2f, center = Offset(marblePx - r * 0.3f, marblePy - r * 0.32f))
        }

        // HUD
        Text(
            text = "Level ${MazeData.levels.indexOf(viewModel.curLvl) + 1}",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 15.sp,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        )
        Text(
            text = "⏱ %.1fs".format(state.elapsedSeconds),
            color = Color(0xFF00E5FF),
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        )
        Button(
            onClick = { viewModel.restartLvl() },
            modifier = Modifier.align(Alignment.BottomStart).padding(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1F3A))
        ) {
            Text("↺ Restart", color = Color.White, fontSize = 13.sp)
        }

        if (!sensorHandler.isAvailable) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xEE0D1021)), contentAlignment = Alignment.Center) {
                Text("⚠️ No gyroscope detected on this device.", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

private fun calculateScore(elapsedSeconds: Float): Int {
    val base = 1200
    val penalty = (elapsedSeconds * 10).toInt()
    return maxOf(0, base - penalty)
}
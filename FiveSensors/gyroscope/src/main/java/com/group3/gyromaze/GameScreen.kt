package com.group3.gyromaze

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group3.gyromaze.model.TileType
import com.group3.gyromaze.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    sensorHandler: SensorHandler,
    onLvlComplete: (score: Int) -> Unit,
    onLvlFailed: (score: Int) -> Unit,
    onReturn: () -> Unit
) {
    val state by viewModel.gameState.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val lvl = viewModel.curLvl
    val doorsOpen = viewModel.doorsAreOpen
    val doorAngle by animateFloatAsState(
        targetValue = if (doorsOpen) 90f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "doorAngle"
    )

    LaunchedEffect(state.isLvlComplete) {
        if (state.isLvlComplete)
            onLvlComplete(calculateScore(state.elapsedSeconds))
    }

    LaunchedEffect(Unit) {
        while (true) {
            if (!isPaused) viewModel.updateTilt(sensorHandler.tiltX, sensorHandler.tiltY)
            delay(16)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1021))
    ) {

        // ------ Main game canvas ------
        Canvas(modifier = Modifier.fillMaxSize()) {
            val tileSize = minOf(size.width / lvl.cols, size.height / lvl.rows)
            val offsetX = (size.width - tileSize * lvl.cols) / 2f
            val offsetY = (size.height - tileSize * lvl.rows) / 2f

            for (row in 0 until lvl.rows) {
                for (col in 0 until lvl.cols) {
                    val rawTile = lvl.grid[row][col]
                    val tile = when (rawTile) {
                        TileType.DOOR_CLOSED, TileType.DOOR_OPEN ->
                            if (doorsOpen) TileType.DOOR_OPEN else TileType.DOOR_CLOSED
                        else -> rawTile
                    }
                    val left = offsetX + col * tileSize
                    val top = offsetY + row * tileSize
                    val tileRect = Size(tileSize, tileSize)

                    when (tile) {
                        TileType.WALL -> {
                            drawRect(color = Color(0xFF1C2333), topLeft = Offset(left, top), size = tileRect)
                            drawRect(color = Color(0xFF2D3A50), topLeft = Offset(left + 2f, top + 2f),
                                size = Size(tileSize - 4f, tileSize - 4f))
                            drawRect(color = Color(0xFF0D1117), topLeft = Offset(left, top), size = tileRect, style = Stroke(width = 1f))
                        }
                        TileType.GOAL -> {
                            drawRect(color = Color(0xFF1A1F3A), topLeft = Offset(left, top), size = tileRect)
                            val cx = left + tileSize / 2f
                            val cy = top + tileSize / 2f
                            val hr = tileSize * 0.38f
                            drawCircle(color = Color(0x99000000), radius = hr * 1.25f, center = Offset(cx, cy))
                            drawCircle(
                                brush = Brush.radialGradient(listOf(Color.Black, Color(0xFF0A0A0A), Color(0xFF222222)),
                                    center = Offset(cx, cy),
                                    radius = hr),
                                radius = hr, center = Offset(cx, cy)
                            )
                            drawCircle(color = Color(0x55FFFFFF), radius = hr, center = Offset(cx, cy), style = Stroke(1.5f))
                        }
                        TileType.ICED_FLOOR -> {
                            drawRect(color = Color(0xFF1A2A4A), topLeft = Offset(left, top), size = tileRect)
                            drawLine(color = Color(0x8800E5FF),
                                start = Offset(left + tileSize * 0.2f, top + tileSize * 0.1f),
                                end = Offset(left + tileSize * 0.5f, top + tileSize * 0.4f),
                                strokeWidth = tileSize * 0.06f)
                            drawRect(color = Color(0x2200E5FF), topLeft = Offset(left, top), size = tileRect, style = Stroke(0.5f))
                        }
                        TileType.TELEPORTER -> {
                            drawRect(color = Color(0xFF1A0033), topLeft = Offset(left, top), size = tileRect)
                            val cx = left + tileSize / 2f
                            val cy = top + tileSize / 2f
                            drawCircle(
                                brush = Brush.radialGradient(listOf(Color(0xFFBB86FC), Color(0x00BB86FC)),
                                    center = Offset(cx, cy), radius = tileSize * 0.48f),
                                radius = tileSize * 0.48f, center = Offset(cx, cy)
                            )
                            drawCircle(color = Color(0xAABB86FC), radius = tileSize * 0.22f, center = Offset(cx, cy))
                            drawCircle(color = Color(0x55FFFFFF), radius = tileSize * 0.22f, center = Offset(cx, cy),
                                style = Stroke(1.5f))
                        }

                        // ------ Animated door — thin plank, not a full wall ------
                        // The panel is only tileSize * 0.12f tall (the short axis),
                        // centred vertically in the tile. At 0° it sits flush across
                        // the passage like a closed barrier. At 90° it rotates flat
                        // against the hinge wall, leaving the passage fully open.
                        TileType.DOOR_OPEN, TileType.DOOR_CLOSED -> {
                            drawRect(color = Color(0xFF1A1F3A), topLeft = Offset(left, top), size = tileRect)
                            val pivotX = left
                            val pivotY = top + tileSize / 2f
                            val panelThickness = tileSize * 0.12f
                            rotate(degrees = -doorAngle, pivot = Offset(pivotX, pivotY)) {
                                // Thin door plank: spans full tile width, thin height
                                drawRect(
                                    color = Color(0xFF8B5E3C),
                                    topLeft = Offset(left, pivotY - panelThickness / 2f),
                                    size = Size(tileSize * 0.92f, panelThickness)
                                )
                                // Edge highlight
                                drawRect(
                                    color = Color(0xFFD4A96A),
                                    topLeft = Offset(left, pivotY - panelThickness / 2f),
                                    size = Size(tileSize * 0.92f, panelThickness),
                                    style = Stroke(1.5f)
                                )
                            }
                        }

                        else -> {
                            drawRect(color = Color(0xFF1A1F3A), topLeft = Offset(left, top), size = tileRect)
                            drawRect(color = Color(0x18FFFFFF), topLeft = Offset(left, top), size = tileRect, style = Stroke(0.5f))
                        }
                    }
                }
            }

            // ------ Marble ------
            val mx = offsetX + state.marblePos.x * tileSize
            val my = offsetY + state.marblePos.y * tileSize
            val r = tileSize * 0.38f
            drawCircle(color = Color(0x55000000), radius = r * 0.85f, center = Offset(mx + r * 0.12f, my + r * 0.18f))
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE8E8E8), Color(0xFFAAAAAA), Color(0xFF404040)),
                    center = Offset(mx - r * 0.3f, my - r * 0.3f),
                    radius = r * 1.4f
                ),
                radius = r, center = Offset(mx, my)
            )
            drawCircle(color = Color.White.copy(alpha = 0.9f), radius = r * 0.18f,
                center = Offset(mx - r * 0.3f, my - r * 0.32f))
        }

        // ------ HUD: level top-left, timer top-centre, pause top-right ------
        Text(
            text = "Level ${MazeData.levels.indexOf(viewModel.curLvl) + 1}",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        )
        Text(
            text = "%.1fs".format(state.elapsedSeconds),
            color = NeonCyan,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
        )
        // ------ Pause button — Material icon, no emoji ------
        IconButton(
            onClick = { viewModel.togglePause() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .size(40.dp)
                .background(Color(0xFF1A1F3A), CircleShape)
                .border(1.dp, NeonPurple.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Rounded.Pause,
                contentDescription = "Pause",
                tint = NeonCyan,
                modifier = Modifier.size(22.dp)
            )
        }

        // ------ Tutorial popup ------
        if (lvl.tutorialMessage != null && !state.isTimerRunning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(NavyCard, RoundedCornerShape(20.dp))
                        .border(1.5.dp, Brush.verticalGradient(listOf(NeonCyan, NeonPurple)), RoundedCornerShape(20.dp))
                        .padding(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Level ${MazeData.levels.indexOf(lvl) + 1}",
                            color = NeonCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        // ------ Close button — Material icon ------
                        IconButton(onClick = { viewModel.startTimer() }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Dismiss",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = lvl.tutorialMessage,
                        color = TextWhite,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(20.dp))
                    WireframeButton(label = "Play!", onClick = { viewModel.startTimer() })
                }
            }
        }

        // ------ Pause overlay ------
        if (isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xBB000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .background(NavyCard, RoundedCornerShape(20.dp))
                        .border(1.5.dp, Brush.verticalGradient(listOf(NeonCyan, NeonPurple)), RoundedCornerShape(20.dp))
                        .padding(28.dp)
                ) {
                    Text("Game Paused", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Spacer(Modifier.height(24.dp))
                    WireframeButton(label = "Resume", onClick = { viewModel.togglePause() })
                    Spacer(Modifier.height(12.dp))
                    WireframeButton(label = "Restart Level", onClick = { viewModel.restartLvl() })
                    Spacer(Modifier.height(12.dp))
                    WireframeButton(label = "Main Menu", onClick = { viewModel.togglePause(); onReturn() })
                }
            }
        }

        if (!sensorHandler.isAvailable) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xEE0D1021)), contentAlignment = Alignment.Center) {
                Text("No gyroscope detected on this device.", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

private fun calculateScore(elapsedSeconds: Float): Int {
    val base = 1200
    val penalty = (elapsedSeconds * 10).toInt()
    return maxOf(0, base - penalty)
}
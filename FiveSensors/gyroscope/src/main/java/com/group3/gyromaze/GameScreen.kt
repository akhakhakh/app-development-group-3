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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group3.gyromaze.model.TileType

// Color palette for the game
object GameColors {
    val wall = Color(0xFF2C3E50)
    val floor = Color(0xFFF0E6D3)
    val goal = Color(0xFF27AE60)
    val ice = Color(0xFFADD8E6)
    val teleporter = Color(0xFF9B59B6)
    val doorOpen = Color(0xFFF39C12)
    val doorClosed = Color(0xFFE74C3C)
    val marble = Color(0xFF3498DB)
    val marbleShine = Color(0x88FFFFFF)
    val background = Color(0xFF1A1A2E)
}

@Composable
fun GameScreen(viewModel: GameViewModel, sensorHandler: SensorHandler) {
    val state by viewModel.gameState.collectAsState()
    val level = viewModel.currentLevel

    // Read sensor tilt and feed it to the ViewModel every frame
    // LaunchedEffect runs once when the composable enters the screen
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.updateTilt(sensorHandler.tiltX, sensorHandler.tiltY)
            kotlinx.coroutines.delay(16)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GameColors.background)
    ) {
        // The game canvas — draws the maze and marble
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Calculate tile size to fit the maze in the canvas
            val tileW = canvasWidth / level.cols
            val tileH = canvasHeight / level.rows
            val tileSize = minOf(tileW, tileH)

            // Center the maze on screen
            val offsetX = (canvasWidth - tileSize * level.cols) / 2
            val offsetY = (canvasHeight - tileSize * level.rows) / 2

            // Draw each tile
            for (row in 0 until level.rows) {
                for (col in 0 until level.cols) {
                    val tile = level.grid[row][col]
                    val color = when (tile) {
                        TileType.WALL -> GameColors.wall
                        TileType.REG_FLOOR -> GameColors.floor
                        TileType.GOAL -> GameColors.goal
                        TileType.ICED_FLOOR -> GameColors.ice
                        TileType.TELEPORTER -> GameColors.teleporter
                        TileType.DOOR_OPEN -> GameColors.doorOpen
                        TileType.DOOR_CLOSED -> GameColors.doorClosed
                    }
                    drawRect(
                        color = color,
                        topLeft = Offset(offsetX + col * tileSize, offsetY + row * tileSize),
                        size = Size(tileSize, tileSize)
                    )
                }
            }

            // Draw the marble
            val marblePixelX = offsetX + state.marblePos.x * tileSize
            val marblePixelY = offsetY + state.marblePos.y * tileSize
            val marbleRadius = tileSize * 0.4f

            // Main marble circle
            drawCircle(
                color = GameColors.marble,
                radius = marbleRadius,
                center = Offset(marblePixelX, marblePixelY)
            )
            // Shine highlight — makes it look 3D
            drawCircle(
                color = GameColors.marbleShine,
                radius = marbleRadius * 0.4f,
                center = Offset(marblePixelX - marbleRadius * 0.25f, marblePixelY - marbleRadius * 0.25f)
            )
        }

        // Timer display
        Text(
            text = "%.1fs".format(state.elapsedSeconds),
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )

        // Restart button
        Button(
            onClick = { viewModel.restartLevel() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text("Restart")
        }

        // Level complete overlay
        if (state.isLevelComplete) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC000000)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Level Complete!", color = Color.White, fontSize = 28.sp)
                    Text("Time: %.1fs".format(state.elapsedSeconds), color = Color.White, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.nextLevel() }) {
                        Text("Next Level")
                    }
                }
            }
        }

        // no gyromaze warning
        if (!sensorHandler.isAvailable) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xCC000000)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Gyromaze not available on this device.",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}
package com.group3.touchscreen2p.ui

import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group3.touchscreen2p.Constants
import com.group3.touchscreen2p.model.Phase
import com.group3.touchscreen2p.model.TargetType
import com.group3.touchscreen2p.ui.theme.BlueDivider
import com.group3.touchscreen2p.ui.theme.BluePlayer2
import com.group3.touchscreen2p.ui.theme.BombRed
import com.group3.touchscreen2p.ui.theme.GreyText
import com.group3.touchscreen2p.ui.theme.NavyBackground
import com.group3.touchscreen2p.ui.theme.NavySurface
import com.group3.touchscreen2p.ui.theme.OrangePlayer1
import com.group3.touchscreen2p.ui.theme.Yellow
import com.group3.touchscreen2p.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onGameOver: (winner: Int, score1: Int, score2: Int, bestCombo: Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startGame()
    }

    LaunchedEffect(state.phase) {
        if (state.phase == Phase.GAME_OVER) {
            onGameOver(state.winner, state.score1, state.score2, state.bestCombo)
        }
    }

    val density = LocalDensity.current
    //Canvas use pixels while Constants use dp. Local Density gives the current screen density to
    // convert to pixels
    val targetRadiusPx = with(density) { Constants.TARGET_RADIUS_DP.dp.toPx() }
    val stateRef = rememberUpdatedState(state)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Press) {
                            event.changes.forEach { change ->
                                val tapX = change.position.x
                                val tapY = change.position.y
                                val nx = tapX / size.width
                                val ny = tapY / size.height
                                val player = if (ny > 0.5f) 1 else 2

                                val hitTarget = stateRef.value.targets
                                    .filter { it.player == player }
                                    .minByOrNull { t ->
                                        val dx = tapX - t.normalizedX * size.width
                                        val dy = tapY - t.normalizedY * size.height
                                        dx * dx + dy * dy
                                    }
                                    ?.takeIf { t ->
                                        val dx = tapX - t.normalizedX * size.width
                                        val dy = tapY - t.normalizedY * size.height
                                        val r = targetRadiusPx * (Constants.TARGET_SHRINK_FACTOR
                                                + (1f - Constants.TARGET_SHRINK_FACTOR) * t.progress)
                                        dx * dx + dy * dy <= r * r
                                    }

                                if (hitTarget != null) {
                                    viewModel.onTargetHit(player, hitTarget, nx, ny)
                                }
                            }

                        }
                    }
                }

            }
    ) {
        // --- Canvas: dividing line + targets ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = BlueDivider,
                start = Offset(0f, size.height / 2f),
                end = Offset(size.width, size.height / 2f),
                strokeWidth = 3.dp.toPx()
            )

            state.targets.forEach { target ->
                val r = targetRadiusPx * (Constants.TARGET_SHRINK_FACTOR + (1f -
                        Constants.TARGET_SHRINK_FACTOR) * target.progress)
                val cx = target.normalizedX * size.width
                val cy = target.normalizedY * size.height
                val color = when (target.type) {
                    TargetType.BULLSEYE -> if (target.player == 1) OrangePlayer1 else BluePlayer2
                    TargetType.TRICK -> Yellow
                    TargetType.BOMB -> BombRed
                }

                // Outer ring
                drawCircle(
                    color = color.copy(alpha = 0.25f),
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.dp.toPx())
                )

                // Middle ring
                drawCircle(
                    color = color.copy(alpha = 0.55f),
                    radius = r * 0.65f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.dp.toPx())
                )

                // Bullseye
                drawCircle(
                    color = color,
                    radius = r * 0.35f,
                    center = Offset(cx, cy)
                )

                // Label for special targets
                val label = when (target.type) {
                    TargetType.TRICK -> "!"
                    TargetType.BOMB  -> "X"
                    else             -> null
                }
                if (label != null) {
                    drawContext.canvas.nativeCanvas.drawText(
                        label,
                        cx,
                        cy + r * 0.35f * 0.4f, // vertically center in the bullseye
                        Paint().apply {
                            this.color = if (label == "!") Color.WHITE else Color.BLACK
                            textSize = targetRadiusPx * 0.45f
                            textAlign = Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                    )
                }


                // Timer arc — starts full, shrinks to 0 as target expires
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = target.progress * 360f,
                    useCenter = false,
                    topLeft = Offset(cx - targetRadiusPx, cy - targetRadiusPx),
                    size = Size(targetRadiusPx * 2f, targetRadiusPx * 2f),
                    style = Stroke(width = 3.5.dp.toPx())
                )
            }

            // Floating effects
            val now = SystemClock.elapsedRealtime()
            state.floatingEffects.forEach { effect ->
                val progress = ((now - effect.startTimeMs) /
                        Constants.FLOATING_EFFECT_DURATION_MS.toFloat()).coerceIn(0f, 1f)
                val cx = effect.normalizedX * size.width
                val cy = effect.normalizedY * size.height - (with(density) { 60.dp.toPx() } * progress)
                val effectColor = when (effect.type) {
                    TargetType.BULLSEYE -> if (effect.player == 1) OrangePlayer1 else BluePlayer2
                    TargetType.TRICK -> Yellow
                    TargetType.BOMB -> BombRed
                }

                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = effectColor.copy(alpha = 1f - progress).toArgb()
                        textSize = with(density) { 20.sp.toPx() }
                        textAlign = Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                    canvas.nativeCanvas.drawText(effect.text, cx, cy, paint)
                }
            }
        }

        // --- P2 HUD (top, rotated so P2 reads it upright) ---
        PlayerHud(
            player = 2,
            score = state.score2,
            phase = state.phase,
            countdownValue = state.countdownValue,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .graphicsLayer { rotationZ = 180f }
        )

        // --- P1 HUD (bottom) ---
        PlayerHud(
            player = 1,
            score = state.score1,
            phase = state.phase,
            countdownValue = state.countdownValue,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )

        // --- Countdown overlay ---
        if (state.phase == Phase.COUNTDOWN) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${state.countdownValue}",
                    fontSize = 96.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Yellow.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun PlayerHud(
    player: Int,
    score: Int,
    phase: Phase,
    countdownValue: Int,
    modifier: Modifier = Modifier
) {
    val playerColor = if (player == 1) OrangePlayer1 else BluePlayer2

    Box(
        modifier = modifier
            .height(72.dp)
            .background(NavySurface.copy(alpha = 0.85f))
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = "P$player",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = playerColor.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = "$score",
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = playerColor,
            modifier = Modifier.align(Alignment.Center)
        )
        if (phase == Phase.COUNTDOWN) {
            Text(
                text = "$countdownValue",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Yellow,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        } else {
            Text(
                text = "→ ${Constants.WIN_SCORE}",
                fontSize = 12.sp,
                color = GreyText,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}



package com.group3.microphone

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group3.microphone.permission.MicPermissionState
import com.group3.microphone.permission.rememberMicPermissionState
import com.group3.microphone.viewmodel.GameViewModel

private val Yellow = Color(0xFFFFD426)
private val CharDark = Color(0xFF1A2035)
private val BgBlue = Color(0xFF4890D1)
private val SkyBlue = Color(0xFF87CEEB)

private const val PLATFORM_HEIGHT_FRACTION = 0.028f

@Composable
fun GameScreen(
    sensitivity: Float = 0.5f,
    onHome: () -> Unit = {},
    gameViewModel: GameViewModel = viewModel()
) {
    val context = LocalContext.current
    val micPermissionState = rememberMicPermissionState(context)
    val amplitude by gameViewModel.amplitude.collectAsState()
    val jumpAmplitude by gameViewModel.jumpAmplitude.collectAsState()
    val platforms by gameViewModel.platforms.collectAsState()
    val characterY by gameViewModel.characterY.collectAsState()
    val isGameOver by gameViewModel.isGameOver.collectAsState()
    val isPaused by gameViewModel.isPaused.collectAsState()
    val score by gameViewModel.score.collectAsState()
    val bestScore by gameViewModel.bestScore.collectAsState()

    LaunchedEffect(Unit) {
        gameViewModel.startPlatforms()
        if (!micPermissionState.isGranted) micPermissionState.requestPermission()
    }

    LaunchedEffect(micPermissionState.isGranted) {
        if (micPermissionState.isGranted) gameViewModel.startListening()
    }

    LaunchedEffect(isPaused) {
        if (!isPaused && micPermissionState.isGranted) gameViewModel.startListening()
    }

    LaunchedEffect(sensitivity) {
        gameViewModel.setSensitivity(sensitivity)
    }

    DisposableEffect(Unit) {
        onDispose {
            gameViewModel.stopListening()
            gameViewModel.stopPlatforms()
        }
    }

    GameScreenContent(
        micPermissionState = micPermissionState,
        amplitude = amplitude,
        jumpAmplitude = jumpAmplitude,
        platforms = platforms,
        characterY = characterY,
        isGameOver = isGameOver,
        isPaused = isPaused,
        score = score,
        bestScore = bestScore,
        onPause = { gameViewModel.pause() },
        onResume = { gameViewModel.resume() },
        onHome = onHome,
        onRestart = { gameViewModel.resetGame() },
        onShare = {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "I scored $score in VoiceJump! Can you beat me?")
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share your score"))
        }
    )
}

@Composable
fun GameScreenContent(
    micPermissionState: MicPermissionState,
    amplitude: Float = 0f,
    jumpAmplitude: Float = 0f,
    platforms: List<Platform> = emptyList(),
    characterY: Float = INITIAL_CHARACTER_Y,
    isGameOver: Boolean = false,
    isPaused: Boolean = false,
    score: Int = 0,
    bestScore: Int = 0,
    onPause: () -> Unit = {},
    onResume: () -> Unit = {},
    onHome: () -> Unit = {},
    onRestart: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SkyBlue
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
        ) {
            // ── Game canvas ──────────────────────────────────────────────────
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val density = LocalDensity.current

                // Slow parallax cloud animation
                val infiniteTransition = rememberInfiniteTransition(label = "clouds")
                val cloudShift by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 0.07f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 22000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "cloudShift"
                )

                val cloudWhite = Color.White.copy(alpha = 0.55f)
                val cloudTint = Color(0xFFD8EEFF).copy(alpha = 0.45f)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    // ── Clouds ───────────────────────────────────────────────
                    // Cloud 1 – upper left, drifts right slowly
                    drawOval(
                        color = cloudWhite,
                        topLeft = Offset(size.width * (0.03f + cloudShift), size.height * 0.07f),
                        size = Size(size.width * 0.26f, size.height * 0.055f)
                    )
                    drawOval(
                        color = cloudWhite,
                        topLeft = Offset(size.width * (0.09f + cloudShift), size.height * 0.045f),
                        size = Size(size.width * 0.16f, size.height * 0.065f)
                    )

                    // Cloud 2 – upper right, drifts left (counter-direction for depth)
                    drawOval(
                        color = cloudWhite,
                        topLeft = Offset(size.width * (0.52f - cloudShift * 0.6f), size.height * 0.13f),
                        size = Size(size.width * 0.33f, size.height * 0.05f)
                    )
                    drawOval(
                        color = cloudWhite,
                        topLeft = Offset(size.width * (0.58f - cloudShift * 0.6f), size.height * 0.10f),
                        size = Size(size.width * 0.20f, size.height * 0.06f)
                    )

                    // Cloud 3 – upper-right zone; smaller and lighter for sense of depth.
                    // Kept at y ≤ 0.24 so it never overlaps the platform spawn zone (≥ 0.35).
                    drawOval(
                        color = cloudTint,
                        topLeft = Offset(size.width * (0.65f + cloudShift * 0.4f), size.height * 0.22f),
                        size = Size(size.width * 0.24f, size.height * 0.04f)
                    )
                    drawOval(
                        color = cloudTint,
                        topLeft = Offset(size.width * (0.70f + cloudShift * 0.4f), size.height * 0.20f),
                        size = Size(size.width * 0.15f, size.height * 0.045f)
                    )

                    // ── Platforms ─────────────────────────────────────────────
                    val platformH = PLATFORM_HEIGHT_FRACTION * size.height
                    platforms.forEach { p ->
                        val left = p.x * size.width
                        val top = p.yFraction * size.height
                        val w = p.widthFraction * size.width
                        if (left < size.width && left + w > 0f) {
                            // Moving platforms rendered in orange so players can identify them.
                            val platformColor = if (p.moveAmplitude > 0f) Color(0xFFE87C2A) else CharDark
                            drawRoundRect(
                                color = platformColor,
                                topLeft = Offset(left, top),
                                size = Size(w, platformH),
                                cornerRadius = CornerRadius(platformH / 2)
                            )
                        }
                    }
                }

                // ── Ninja character ──────────────────────────────────────────
                // Anchor the ninja's FEET to the physics bottom so it stands on platforms.
                // NinjaCharacter: 30dp head + 62dp body + 4dp spacer + 9dp belt = 105dp tall.
                // Belt (feet) is 70dp wide, so center offset = 35dp.
                val charCenterXDp = with(density) { (CHARACTER_X_FRACTION * constraints.maxWidth).toDp() }
                val physicsBottomDp = with(density) {
                    ((characterY + CHARACTER_HEIGHT_FRACTION) * constraints.maxHeight).toDp()
                }

                Box(
                    modifier = Modifier.absoluteOffset(
                        x = charCenterXDp - 35.dp,
                        y = physicsBottomDp - 105.dp
                    )
                ) {
                    NinjaCharacter()
                }

                // ── Score display ────────────────────────────────────────────
                Text(
                    text = "SCORE  $score",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 12.dp, end = 14.dp)
                )

                // ── Pause button ─────────────────────────────────────────────
                if (!isGameOver) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 8.dp, start = 12.dp)
                            .size(40.dp)
                            .background(Yellow, CircleShape)
                            .clickable { onPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(16.dp)) {
                            val barW = size.width * 0.28f
                            val barH = size.height * 0.75f
                            val top = (size.height - barH) / 2f
                            val gap = size.width * 0.22f
                            val totalW = barW * 2 + gap
                            val startX = (size.width - totalW) / 2f
                            drawRoundRect(
                                color = CharDark,
                                topLeft = androidx.compose.ui.geometry.Offset(startX, top),
                                size = Size(barW, barH),
                                cornerRadius = CornerRadius(barW / 2)
                            )
                            drawRoundRect(
                                color = CharDark,
                                topLeft = androidx.compose.ui.geometry.Offset(startX + barW + gap, top),
                                size = Size(barW, barH),
                                cornerRadius = CornerRadius(barW / 2)
                            )
                        }
                    }
                }

                if (micPermissionState.hasBeenDenied && !micPermissionState.isGranted) {
                    Text(
                        text = "Microphone permission is required. Please grant it in Settings.",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    )
                }

            }

            // ── HUD panel ────────────────────────────────────────────────────
            HudPanel(
                volume = (amplitude / JUMP_MAX_AMPLITUDE).coerceIn(0f, 1f),
                bottomPadding = innerPadding.calculateBottomPadding()
            )
        }

        // ── Game Over overlay ─────────────────────────────────────────────────
        if (isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CharDark.copy(alpha = 0.93f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 48.dp)
                ) {
                    Text(
                        text = "GAME OVER",
                        color = Yellow,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(28.dp))
                    Text(
                        text = "SCORE",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )
                    Text(
                        text = "$score",
                        color = Yellow,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 64.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "BEST  $bestScore",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(36.dp))
                    Button(
                        onClick = onRestart,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Yellow,
                            contentColor = CharDark
                        )
                    ) {
                        Text("PLAY AGAIN", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(2.dp, Yellow),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Yellow)
                    ) {
                        Text("SHARE YOUR SCORE!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = onHome,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(2.dp, Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("BACK TO HOME", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ── Pause overlay ─────────────────────────────────────────────────────
        if (isPaused) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CharDark.copy(alpha = 0.80f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 48.dp)
                ) {
                    Text(
                        text = "PAUSED",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = onResume,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Yellow,
                            contentColor = CharDark
                        )
                    ) {
                        Text("RESUME", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = onHome,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(2.dp, Color.White),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("BACK TO HOME", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        } // end outer Box
    }
}

@Composable
private fun HudPanel(volume: Float, bottomPadding: Dp = 0.dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CharDark)
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 16.dp + bottomPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MicIcon()
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "VOICE VOLUME",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            VolumeBar(progress = volume)
        }
    }
}

@Composable
private fun MicIcon() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(BgBlue, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(28.dp)) {
            val w = size.width; val h = size.height
            val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            val bodyW = w * 0.38f; val bodyH = h * 0.44f
            val bodyL = (w - bodyW) / 2f; val bodyT = h * 0.05f
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(bodyL, bodyT),
                size = Size(bodyW, bodyH),
                cornerRadius = CornerRadius(bodyW / 2f)
            )
            val arcH = h * 0.28f; val arcT = bodyT + bodyH - arcH / 2f
            drawArc(
                color = Color.White, startAngle = 0f, sweepAngle = 180f, useCenter = false,
                topLeft = Offset(bodyL, arcT), size = Size(bodyW, arcH), style = stroke
            )
            val px = w / 2f; val pb = h * 0.88f
            drawLine(Color.White, Offset(px, arcT + arcH), Offset(px, pb), 1.8.dp.toPx(), StrokeCap.Round)
            drawLine(Color.White, Offset(px - w * 0.18f, pb), Offset(px + w * 0.18f, pb), 1.8.dp.toPx(), StrokeCap.Round)
        }
    }
}

@Composable
private fun VolumeBar(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(5.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(Yellow, RoundedCornerShape(5.dp))
        )
    }
}

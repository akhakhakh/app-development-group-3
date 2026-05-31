package com.group3.camera

import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

// ─── Timing constants ────────────────────────────────────────────────────────

private const val BPM             = 90
private val    BEAT_MS            = 60_000L / BPM   // 667 ms
private const val LEAD_IN_BEATS   = 8               // 2 bars before first expression
private const val COUNTDOWN_BEATS = 4               // 1 bar of countdown per expression

// (expressionCount, restBeats after snap) — total = 20 expressions
private val PHASES = listOf(
    5 to 8,   // intro:  5 expressions, 8-beat (2-bar) gap  → ~8.0s each
    7 to 4,   // build:  7 expressions, 4-beat (1-bar) gap  → ~5.3s each
    6 to 2,   // peak:   6 expressions, 2-beat gap          → ~4.0s each
    2 to 0,   // finale: 2 expressions, back-to-back        → ~2.7s each
)

// ─── Expression data ─────────────────────────────────────────────────────────

private data class GameExpression(
    val name: String,
    @DrawableRes val drawableId: Int,
)

private val allGameExpressions = listOf(
    GameExpression("SMILE",         R.drawable.smile),
    GameExpression("WINK LEFT",     R.drawable.neutral_leftwink),
    GameExpression("WINK RIGHT",    R.drawable.neutral_rightwink),
    GameExpression("CLOSE EYES",    R.drawable.neutral_closeeyes),
    GameExpression("LOOK LEFT",     R.drawable.neutral_lookleft),
    GameExpression("LOOK RIGHT",    R.drawable.neutral_lookright),
    GameExpression("LOOK UP",       R.drawable.neutral_lookup),
    GameExpression("LOOK DOWN",     R.drawable.neutral_lookdown),
    GameExpression("TILT LEFT",     R.drawable.tiltleft),
    GameExpression("TILT RIGHT",    R.drawable.tiltright),
    GameExpression("SMILE+WINK L",  R.drawable.smile_leftwink),
    GameExpression("SMILE+WINK R",  R.drawable.smile_rightwink),
    GameExpression("SMILE+EYES",    R.drawable.smile_closeeyes),
    GameExpression("SMILE+LOOK L",  R.drawable.smile_lookleft),
    GameExpression("SMILE+LOOK R",  R.drawable.smile_lookright),
    GameExpression("SMILE+LOOK UP", R.drawable.smile_lookup),
    GameExpression("SMILE+LOOK DN", R.drawable.smile_lookdown),
    GameExpression("NEUTRAL",       R.drawable.emoticon_neutral),
)

// ─── Schedule ────────────────────────────────────────────────────────────────

private data class ScheduledExpression(
    val expression: GameExpression,
    val countdownStartBeat: Int,   // beat when the 4-beat countdown begins
    val snapBeat: Int,             // countdownStartBeat + COUNTDOWN_BEATS
)

private fun buildSchedule(): List<ScheduledExpression> {
    val pool = allGameExpressions.shuffled().toMutableList()
    val schedule = mutableListOf<ScheduledExpression>()
    var beat = LEAD_IN_BEATS

    for ((count, restBeats) in PHASES) {
        repeat(count) {
            if (pool.isEmpty()) pool.addAll(allGameExpressions.shuffled())
            // avoid repeating the same expression back-to-back
            var pick = pool.removeFirst()
            if (pick == schedule.lastOrNull()?.expression && pool.isNotEmpty()) {
                pool.addLast(pick)
                pick = pool.removeFirst()
            }
            schedule.add(
                ScheduledExpression(
                    expression         = pick,
                    countdownStartBeat = beat,
                    snapBeat           = beat + COUNTDOWN_BEATS,
                )
            )
            beat += COUNTDOWN_BEATS + restBeats
        }
    }
    return schedule
}

// ─── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun GameScreen(onGameEnd: () -> Unit) {
    val context       = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val schedule     = remember { buildSchedule() }
    val gameStartMs  = remember { SystemClock.elapsedRealtime() }

    var elapsedMs    by remember { mutableLongStateOf(0L) }
    var snappedCount by remember { mutableIntStateOf(0) }
    var showFlash    by remember { mutableStateOf(false) }
    var isDone       by remember { mutableStateOf(false) }

    // Smooth beat timer — always relative to gameStartMs so it never drifts
    LaunchedEffect(Unit) {
        while (!isDone) {
            withFrameMillis { elapsedMs = SystemClock.elapsedRealtime() - gameStartMs }
        }
    }

    // Snap scheduler — fires at the precise millisecond each snap is due
    LaunchedEffect(schedule) {
        for (i in schedule.indices) {
            val snapDueMs = schedule[i].snapBeat * BEAT_MS
            val waitMs    = snapDueMs - (SystemClock.elapsedRealtime() - gameStartMs)
            if (waitMs > 0) delay(waitMs)

            showFlash = true
            // TODO: capture camera frame here for scoring
            delay(180)
            showFlash = false

            snappedCount = i + 1
        }
        isDone = true
    }

    LaunchedEffect(isDone) {
        if (isDone) {
            delay(600)
            onGameEnd()
        }
    }

    DisposableEffect(Unit) {
        onDispose { ProcessCameraProvider.getInstance(context).get().unbindAll() }
    }

    // ── Derived state ──────────────────────────────────────────────────────

    val fractBeat = elapsedMs.toFloat() / BEAT_MS
    val currentScheduled = schedule.getOrNull(snappedCount)

    val isLeadIn = fractBeat < schedule.first().countdownStartBeat

    val isInCountdown = currentScheduled != null &&
        fractBeat >= currentScheduled.countdownStartBeat &&
        fractBeat <  currentScheduled.snapBeat

    // 1.0 = full bar / snap hasn't happened yet, 0.0 = snap moment
    val countdownProgress = when {
        isLeadIn      -> 1f
        isInCountdown -> {
            val beatInWindow = fractBeat - currentScheduled!!.countdownStartBeat
            1f - (beatInWindow / COUNTDOWN_BEATS).coerceIn(0f, 1f)
        }
        else -> 0f
    }

    // ── UI ─────────────────────────────────────────────────────────────────

    Box(modifier = Modifier.fillMaxSize()) {

        // Camera preview (full screen background)
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { previewView ->
                val future = ProcessCameraProvider.getInstance(context)
                future.addListener({
                    val cameraProvider = future.get()
                    val preview = Preview.Builder().build()
                        .also { it.setSurfaceProvider(previewView.surfaceProvider) }
                    cameraProvider.unbindAll()
                    try {
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview
                        )
                    } catch (e: Exception) { e.printStackTrace() }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        // Snap flash
        if (showFlash) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.65f))
            )
        }

        // Top HUD
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color(0xDD0F0F14)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLeadIn -> Text(
                        "Get ready!",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    isInCountdown -> {
                        // beats remaining: 3 on first beat, 2, 1, then SNAP on last
                        val beatInWindow = (fractBeat - currentScheduled!!.countdownStartBeat).toInt()
                        val label = when (COUNTDOWN_BEATS - beatInWindow) {
                            in 3..Int.MAX_VALUE -> "3"
                            2                  -> "2"
                            1                  -> "1"
                            else               -> "SNAP"
                        }
                        Text(
                            label,
                            color = if (label == "SNAP") Color(0xFFFAD12E) else Color.White,
                            fontSize = if (label == "SNAP") 18.sp else 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    else -> Text(
                        "${minOf(snappedCount + 1, schedule.size)} / ${schedule.size}",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }

            // Countdown progress bar: blue → yellow as it empties
            val barColor = if (countdownProgress > 0.3f) Color(0xFF3885F0) else Color(0xFFFAD12E)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .background(Color(0xFF2E2E33))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(countdownProgress)
                        .background(barColor, RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                )
            }
        }

        // Bottom conveyor
        GameConveyor(
            schedule     = schedule,
            currentIdx   = snappedCount,
            totalSnapped = snappedCount,
            modifier     = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

// ─── Conveyor ────────────────────────────────────────────────────────────────

@Composable
private fun GameConveyor(
    schedule: List<ScheduledExpression>,
    currentIdx: Int,
    totalSnapped: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color(0xFFFAFAFA))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = "NEXT UP",
            color = Color(0xFF8C8C91),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Active card
            schedule.getOrNull(currentIdx)?.let { active ->
                ExpressionQueueCard(
                    expr     = active.expression,
                    isActive = true,
                    size     = 90.dp,
                )
            }

            // Up to 3 upcoming cards
            for (offset in 1..3) {
                schedule.getOrNull(currentIdx + offset)?.let { upcoming ->
                    ExpressionQueueCard(
                        expr     = upcoming.expression,
                        isActive = false,
                        size     = 64.dp,
                    )
                }
            }

            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(8.dp))

        // Game progress bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Expression ${minOf(totalSnapped + 1, schedule.size)} of ${schedule.size}",
                color = Color(0xFF8C8C91),
                fontSize = 9.sp
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(Color(0xFFDBDBE0), RoundedCornerShape(3.dp))
        ) {
            val progress = (totalSnapped.toFloat() / schedule.size).coerceIn(0f, 1f)
            if (progress > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(Color(0xFF3885F0), RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

@Composable
private fun ExpressionQueueCard(
    expr: GameExpression,
    isActive: Boolean,
    size: Dp,
) {
    val bg     = if (isActive) Color(0xFFD9EBFF) else Color(0xFFF0F0F5)
    val border = if (isActive) Color(0xFF3885F0) else Color(0xFFDBDBE0)
    val width  = if (isActive) 2.dp else 1.dp

    Column(
        modifier = Modifier
            .size(size)
            .background(bg, RoundedCornerShape(8.dp))
            .border(width, border, RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter            = painterResource(id = expr.drawableId),
            contentDescription = expr.name,
            modifier           = Modifier.weight(1f).aspectRatio(1f)
        )
        Text(
            text      = expr.name,
            fontSize  = if (isActive) 7.sp else 6.sp,
            fontWeight = FontWeight.Bold,
            color     = if (isActive) Color(0xFF3885F0) else Color(0xFF8C8C91),
            textAlign = TextAlign.Center,
            maxLines  = 2,
            lineHeight = 8.sp,
            modifier  = Modifier.padding(top = 2.dp)
        )
    }
}

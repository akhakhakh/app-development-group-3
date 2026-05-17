package com.group3.camera

import android.graphics.PointF
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlin.math.sqrt

// ── Design tokens (from Figma) ────────────────────────────────────────────
private val FcBlue       = Color(0xFF3885F0)
private val FcGreen      = Color(0xFF40C76B)
private val FcBackground = Color(0xFFD9EBFF)   // light-blue screen background
private val FcTextPri    = Color(0xFF1A1A1F)
private val FcTextSec    = Color(0xFF8C8C91)
private val FcBorder     = Color(0xFFDBDBE0)

// ── Screen ────────────────────────────────────────────────────────────────

@Composable
fun FaceCheckScreen(onNext: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var result by remember { mutableStateOf<FaceCheckResult?>(null) }

    // AD5S-162: track face-centre history to decide "hold still"
    val positionHistory = remember { ArrayDeque<PointF>() }
    var isStill by remember { mutableStateOf(false) }

    // AD5S-163: glasses warning — eye-open probabilities both low
    var showGlassesWarning by remember { mutableStateOf(false) }

    // AD5S-165: hat/accessories hint — head pitched far down (forehead hidden)
    var showHatsHint by remember { mutableStateOf(false) }

    // Derive check states
    val face = result?.faces?.firstOrNull()
    val faceDetected = face != null
    // AD5S-164: acceptable brightness range (not too dark, not overexposed)
    val lightingOk = result?.avgBrightness?.let { it in 0.12f..0.88f } ?: false
    val allPassed = faceDetected && lightingOk && isStill

    // Update stability + warning flags whenever a new frame arrives
    LaunchedEffect(result) {
        val f = result?.faces?.firstOrNull()
        if (f != null) {
            val box = f.boundingBox
            val center = PointF(box.exactCenterX(), box.exactCenterY())
            if (positionHistory.size >= 10) positionHistory.removeFirst()
            positionHistory.addLast(center)

            if (positionHistory.size >= 5) {
                val meanX = positionHistory.map { it.x }.average()
                val meanY = positionHistory.map { it.y }.average()
                val maxDev = positionHistory.maxOf {
                    sqrt(((it.x - meanX) * (it.x - meanX) + (it.y - meanY) * (it.y - meanY)).toDouble()).toFloat()
                }
                // AD5S-162: stable when max deviation < 25 image-pixels
                isStill = maxDev < 25f
            }

            // AD5S-163: both eye-open probabilities low → possible glasses glare
            val leftEye = f.leftEyeOpenProbability ?: 1f
            val rightEye = f.rightEyeOpenProbability ?: 1f
            showGlassesWarning = leftEye < 0.55f && rightEye < 0.55f

            // AD5S-165: large downward pitch → forehead likely obstructed by hat
            showHatsHint = f.headEulerAngleX < -18f
        } else {
            positionHistory.clear()
            isStill = false
            showGlassesWarning = false
            showHatsHint = false
        }
    }

    // Release camera when this screen is disposed (e.g. back-navigation)
    DisposableEffect(Unit) {
        onDispose {
            ProcessCameraProvider.getInstance(context).get().unbindAll()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FcBackground)
    ) {
        // ── App bar ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(FcBlue),
            contentAlignment = Alignment.Center
        ) {
            Text("FACE CHECK", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        // ── Step dots ────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepDot(active = true)
            Spacer(Modifier.width(6.dp))
            StepDot(active = false)
            Spacer(Modifier.width(6.dp))
            StepDot(active = false)
        }

        Text(
            text = "Step 1 of 3  —  Position your face",
            color = FcTextSec,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )

        // ── Camera preview + oval overlay (AD5S-161) ─────────────────────
        val ovalColor by animateColorAsState(
            targetValue = if (faceDetected) FcGreen else FcBlue,
            animationSpec = tween(300),
            label = "oval"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 44.dp, vertical = 10.dp)
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(120.dp))        // clip preview to oval shape
                .border(2.dp, FcBorder, RoundedCornerShape(120.dp))
        ) {
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
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val analyzer = FaceCheckAnalyzer { r -> result = r }
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(ContextCompat.getMainExecutor(context), analyzer)
                            }
                        cameraProvider.unbindAll()
                        try {
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_FRONT_CAMERA,
                                preview, imageAnalysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(context))
                }
            )

            // Oval border drawn on top of the camera (AD5S-161)
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val pad = 6.dp.toPx()
                drawOval(
                    color = ovalColor,
                    topLeft = Offset(pad, pad),
                    size = Size(size.width - pad * 2, size.height - pad * 2),
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }

        // ── Instruction ───────────────────────────────────────────────────
        Text(
            text = "Centre your face inside the oval",
            color = FcTextPri,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(10.dp))

        // ── Check list ────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // AD5S-162: face detected + aligned in oval
            CheckRow(label = "Face detected", passed = faceDetected)
            // AD5S-164: lighting check
            CheckRow(label = "Lighting OK", passed = lightingOk)
            // AD5S-162 (stability): hold still
            CheckRow(label = "Hold still", passed = isStill, warningWhenFailed = true)

            // AD5S-163: glasses warning
            if (showGlassesWarning) {
                TipRow(
                    icon = "👓",
                    text = "Glasses may affect detection — try removing them"
                )
            }
            // AD5S-165: hat/accessories tip
            if (showHatsHint) {
                TipRow(
                    icon = "🎩",
                    text = "Remove hats or accessories that cover your forehead"
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // ── NEXT button ───────────────────────────────────────────────────
        Button(
            onClick = onNext,
            enabled = allPassed,
            modifier = Modifier
                .padding(horizontal = 52.dp)
                .padding(bottom = 24.dp)
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FcBlue,
                disabledContainerColor = FcBlue.copy(alpha = 0.35f)
            )
        ) {
            Text(
                text = "NEXT  →",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// ── Reusable sub-composables ──────────────────────────────────────────────

@Composable
private fun StepDot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(if (active) 14.dp else 10.dp)
            .background(
                color = if (active) FcBlue else FcBorder,
                shape = CircleShape
            )
    )
}

@Composable
private fun CheckRow(
    label: String,
    passed: Boolean,
    warningWhenFailed: Boolean = false
) {
    val borderColor = when {
        passed -> FcGreen
        warningWhenFailed -> Color(0xFFFAD12E)
        else -> FcBorder
    }
    val iconBg = when {
        passed -> FcGreen
        warningWhenFailed -> Color(0xFFFAD12E)
        else -> FcBorder
    }
    val icon = when {
        passed -> "✓"
        warningWhenFailed -> "⚠"
        else -> "…"
    }
    val iconColor = if (warningWhenFailed && !passed) FcTextPri else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = iconColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(10.dp))
        Text(label, color = FcTextPri, fontSize = 12.sp)
    }
}

@Composable
private fun TipRow(icon: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF8DC), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE8D070), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 14.sp)
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color(0xFF5C4500), fontSize = 11.sp)
    }
}

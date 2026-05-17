package com.group3.camera

import android.graphics.PointF
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceLandmark

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var faces by remember { mutableStateOf<List<Face>>(emptyList()) }
    var imageWidth by remember { mutableIntStateOf(1) }
    var imageHeight by remember { mutableIntStateOf(1) }
    var imageRotation by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {

        // --- Camera preview ---
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

                    val analyzer = FaceAnalyzer { detectedFaces, w, h, rotation ->
                        faces = detectedFaces
                        imageWidth = w
                        imageHeight = h
                        imageRotation = rotation
                    }
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { it.setAnalyzer(ContextCompat.getMainExecutor(context), analyzer) }

                    cameraProvider.unbindAll()
                    try {
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_FRONT_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        // --- Face feature overlay ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            // After ML Kit applies rotation, the logical image dimensions may be swapped
            val (logicalW, logicalH) = if (imageRotation == 90 || imageRotation == 270)
                imageHeight to imageWidth
            else
                imageWidth to imageHeight

            for (face in faces) {
                drawFaceOverlay(face, logicalW, logicalH)
            }
        }

        // --- Detection info panel (bottom) ---
        faces.firstOrNull()?.let { face ->
            FaceInfoPanel(face, modifier = Modifier.align(Alignment.BottomCenter))
        }

        // "No face" hint when nothing is detected
        if (faces.isEmpty()) {
            Text(
                text = "Point front camera at a face",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Canvas drawing helpers
// ---------------------------------------------------------------------------

private fun DrawScope.drawFaceOverlay(face: Face, imageW: Int, imageH: Int) {
    // Front camera mirror: flip X so overlay aligns with the mirrored preview
    fun toX(px: Float) = (1f - px / imageW) * size.width
    fun toY(py: Float) = (py / imageH) * size.height
    fun pt(p: PointF) = Offset(toX(p.x), toY(p.y))

    // Bounding box (yellow)
    val box = face.boundingBox
    // left/right swapped because of the front-camera mirror
    val left = toX(box.right.toFloat())
    val right = toX(box.left.toFloat())
    val top = toY(box.top.toFloat())
    val bottom = toY(box.bottom.toFloat())
    drawRect(
        color = Color.Yellow,
        topLeft = Offset(left, top),
        size = Size(right - left, bottom - top),
        style = Stroke(width = 4f)
    )

    // Contours (cyan) — 13 contour groups trace the face geometry
    val contourTypes = listOf(
        FaceContour.FACE,
        FaceContour.LEFT_EYEBROW_TOP, FaceContour.LEFT_EYEBROW_BOTTOM,
        FaceContour.RIGHT_EYEBROW_TOP, FaceContour.RIGHT_EYEBROW_BOTTOM,
        FaceContour.LEFT_EYE, FaceContour.RIGHT_EYE,
        FaceContour.UPPER_LIP_TOP, FaceContour.UPPER_LIP_BOTTOM,
        FaceContour.LOWER_LIP_TOP, FaceContour.LOWER_LIP_BOTTOM,
        FaceContour.NOSE_BRIDGE, FaceContour.NOSE_BOTTOM
    )
    for (type in contourTypes) {
        val pts = face.getContour(type)?.points ?: continue
        for (i in 0 until pts.size - 1) {
            drawLine(color = Color.Cyan, start = pt(pts[i]), end = pt(pts[i + 1]), strokeWidth = 2f)
        }
        // Close loops for eye and face contours
        if (pts.size > 2 && type in listOf(FaceContour.FACE, FaceContour.LEFT_EYE, FaceContour.RIGHT_EYE)) {
            drawLine(color = Color.Cyan, start = pt(pts.last()), end = pt(pts.first()), strokeWidth = 2f)
        }
    }

    // Landmarks (colored dots) — 10 named points
    val landmarks = listOf(
        FaceLandmark.LEFT_EYE to Color(0xFF4CAF50),       // green
        FaceLandmark.RIGHT_EYE to Color(0xFF4CAF50),
        FaceLandmark.NOSE_BASE to Color(0xFFFF9800),       // orange
        FaceLandmark.MOUTH_LEFT to Color(0xFFE91E63),      // pink
        FaceLandmark.MOUTH_RIGHT to Color(0xFFE91E63),
        FaceLandmark.MOUTH_BOTTOM to Color(0xFFE91E63),
        FaceLandmark.LEFT_EAR to Color(0xFF9C27B0),        // purple
        FaceLandmark.RIGHT_EAR to Color(0xFF9C27B0),
        FaceLandmark.LEFT_CHEEK to Color(0xFF2196F3),      // blue
        FaceLandmark.RIGHT_CHEEK to Color(0xFF2196F3)
    )
    for ((type, color) in landmarks) {
        val position = face.getLandmark(type)?.position ?: continue
        drawCircle(color = color, radius = 10f, center = pt(position))
        drawCircle(color = Color.White, radius = 10f, center = pt(position), style = Stroke(width = 2f))
    }
}

// ---------------------------------------------------------------------------
// Info panel
// ---------------------------------------------------------------------------

@Composable
private fun FaceInfoPanel(face: Face, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Color.Black.copy(alpha = 0.65f),
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "ML Kit Face Detection — Live Results",
            color = Color.White,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(8.dp))

        // Classification
        SectionHeader("Classification")
        InfoRow("Smiling", face.smilingProbability.toPercent())
        InfoRow("Left eye open", face.leftEyeOpenProbability.toPercent())
        InfoRow("Right eye open", face.rightEyeOpenProbability.toPercent())

        Spacer(Modifier.height(6.dp))

        // Head pose (Euler angles)
        SectionHeader("Head Pose (Euler angles)")
        InfoRow("Pitch  (X) — nod up/down", "%.1f°".format(face.headEulerAngleX))
        InfoRow("Yaw    (Y) — turn left/right", "%.1f°".format(face.headEulerAngleY))
        InfoRow("Roll   (Z) — tilt left/right", "%.1f°".format(face.headEulerAngleZ))

        Spacer(Modifier.height(6.dp))

        // Tracking
        SectionHeader("Tracking")
        InfoRow("Face ID", face.trackingId?.toString() ?: "N/A")
        InfoRow(
            "Landmarks detected",
            listOf(
                FaceLandmark.LEFT_EYE, FaceLandmark.RIGHT_EYE, FaceLandmark.NOSE_BASE,
                FaceLandmark.MOUTH_LEFT, FaceLandmark.MOUTH_RIGHT, FaceLandmark.MOUTH_BOTTOM,
                FaceLandmark.LEFT_EAR, FaceLandmark.RIGHT_EAR,
                FaceLandmark.LEFT_CHEEK, FaceLandmark.RIGHT_CHEEK
            ).count { face.getLandmark(it) != null }.toString() + " / 10"
        )
        InfoRow(
            "Contours detected",
            listOf(
                FaceContour.FACE,
                FaceContour.LEFT_EYEBROW_TOP, FaceContour.LEFT_EYEBROW_BOTTOM,
                FaceContour.RIGHT_EYEBROW_TOP, FaceContour.RIGHT_EYEBROW_BOTTOM,
                FaceContour.LEFT_EYE, FaceContour.RIGHT_EYE,
                FaceContour.UPPER_LIP_TOP, FaceContour.UPPER_LIP_BOTTOM,
                FaceContour.LOWER_LIP_TOP, FaceContour.LOWER_LIP_BOTTOM,
                FaceContour.NOSE_BRIDGE, FaceContour.NOSE_BOTTOM
            ).count { face.getContour(it) != null }.toString() + " / 13"
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, color = Color(0xFFFFCC00), fontSize = 11.sp)
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.LightGray, fontSize = 11.sp)
        Text(value, color = Color.White, fontSize = 11.sp)
    }
}

private fun Float?.toPercent(): String =
    this?.let { "%.0f%%".format(it * 100) } ?: "N/A"

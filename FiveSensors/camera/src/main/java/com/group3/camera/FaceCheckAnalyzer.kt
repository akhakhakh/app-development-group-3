package com.group3.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

data class FaceCheckResult(
    val faces: List<Face>,
    val avgBrightness: Float,   // 0..1  (AD5S-164)
    val imageWidth: Int,
    val imageHeight: Int,
    val rotationDegrees: Int
)

class FaceCheckAnalyzer(
    private val onResult: (FaceCheckResult) -> Unit
) : ImageAnalysis.Analyzer {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()
    )

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }

        // Sample ~200 pixels from Y (luminance) plane for brightness without
        // consuming the buffer (uses absolute index get to leave position intact).
        val yBuffer = mediaImage.planes[0].buffer
        val yCapacity = yBuffer.remaining()
        val step = maxOf(1, yCapacity / 200)
        var sum = 0L
        var count = 0
        val base = yBuffer.position()
        var i = 0
        while (i < yCapacity) {
            sum += yBuffer.get(base + i).toInt() and 0xFF
            count++
            i += step
        }
        val brightness = if (count > 0) sum.toFloat() / count / 255f else 0.5f

        val rotation = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotation)

        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                onResult(
                    FaceCheckResult(
                        faces = faces,
                        avgBrightness = brightness,
                        imageWidth = imageProxy.width,
                        imageHeight = imageProxy.height,
                        rotationDegrees = rotation
                    )
                )
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}

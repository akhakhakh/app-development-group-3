package com.group3.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceAnalyzer(
    private val onFacesDetected: (faces: List<Face>, imageWidth: Int, imageHeight: Int, rotationDegrees: Int) -> Unit
) : ImageAnalysis.Analyzer {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            // High accuracy mode (vs FAST — better for demo)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            // Detect all 10 landmark points (eyes, nose, ears, mouth, cheeks)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            // Detect all 13 contour groups (face outline, eyebrows, eyes, lips, nose)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            // Enable smiling + eye-open probability classification
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            // Assign a stable ID to each face across frames
            .enableTracking()
            .build()
    )

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }
        val rotation = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotation)

        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                onFacesDetected(faces, imageProxy.width, imageProxy.height, rotation)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}

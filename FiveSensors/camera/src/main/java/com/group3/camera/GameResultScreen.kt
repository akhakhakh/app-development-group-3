package com.group3.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.abs
import kotlin.math.roundToInt

private fun scoreExpression(face: Face, name: String): Float {
    fun smile()     = face.smilingProbability             ?: 0f
    fun leftOpen()  = face.leftEyeOpenProbability         ?: 1f
    fun rightOpen() = face.rightEyeOpenProbability        ?: 1f
    fun yaw()       = face.headEulerAngleY                        
    fun pitch()     = face.headEulerAngleX                        
    fun roll()      = face.headEulerAngleZ                        

    return when (name) {
        "SMILE"         -> smile()
        "NEUTRAL"       -> {
            val notSmiling = 1f - smile()
            val eyesOpen   = (leftOpen() + rightOpen()) / 2f
            val headLevel  = (1f - abs(yaw()) / 20f).coerceIn(0f, 1f)
            (notSmiling * 0.4f + eyesOpen * 0.3f + headLevel * 0.3f)
        }
        "WINK LEFT"     -> (1f - leftOpen()).coerceIn(0f, 1f)
        "WINK RIGHT"    -> (1f - rightOpen()).coerceIn(0f, 1f)
        "CLOSE EYES"    -> minOf(1f - leftOpen(), 1f - rightOpen())
        "LOOK LEFT"     -> (yaw()  / 30f).coerceIn(0f, 1f)
        "LOOK RIGHT"    -> (-yaw() / 30f).coerceIn(0f, 1f)
        "LOOK UP"       -> (pitch()  / 20f).coerceIn(0f, 1f)
        "LOOK DOWN"     -> (-pitch() / 20f).coerceIn(0f, 1f)
        "TILT LEFT"     -> (roll()  / 25f).coerceIn(0f, 1f)
        "TILT RIGHT"    -> (-roll() / 25f).coerceIn(0f, 1f)
        "SMILE+WINK L"  -> (smile() + (1f - leftOpen()).coerceIn(0f, 1f)) / 2f
        "SMILE+WINK R"  -> (smile() + (1f - rightOpen()).coerceIn(0f, 1f)) / 2f
        "SMILE+EYES"    -> (smile() + minOf(1f - leftOpen(), 1f - rightOpen())) / 2f
        "SMILE+LOOK L"  -> (smile() + (yaw()  / 30f).coerceIn(0f, 1f)) / 2f
        "SMILE+LOOK R"  -> (smile() + (-yaw() / 30f).coerceIn(0f, 1f)) / 2f
        "SMILE+LOOK UP" -> (smile() + (pitch()  / 20f).coerceIn(0f, 1f)) / 2f
        "SMILE+LOOK DN" -> (smile() + (-pitch() / 20f).coerceIn(0f, 1f)) / 2f
        else            -> 0f
    }
}

private fun overallGrade(avg: Float): Pair<String, Color> = when {
    avg >= 0.75f -> "A" to Color(0xFF26AA3F)
    avg >= 0.60f -> "B" to Color(0xFF3885F0)
    avg >= 0.45f -> "C" to Color(0xFFFAD12E)
    avg >= 0.30f -> "D" to Color(0xFFF97316)
    avg >= 0.15f -> "E" to Color(0xFFEC4899)
    else         -> "F" to Color(0xFFEF4444)
}


private val detector by lazy {
    FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    )
}

private suspend fun detectFace(result: GameResultStore.CapturedResult): Float =
    suspendCancellableCoroutine { cont ->
        val image = InputImage.fromBitmap(result.bitmap, 0)
        detector.process(image)
            .addOnSuccessListener { faces ->
                val score = faces.firstOrNull()
                    ?.let { scoreExpression(it, result.expressionName) }
                    ?: 0f
                if (cont.isActive) cont.resume(score)
            }
            .addOnFailureListener {
                if (cont.isActive) cont.resume(0f)
            }
    }


private data class ScoredResult(
    val result: GameResultStore.CapturedResult,
    val score: Float,
)

@Composable
fun GameResultScreen(onPlayAgain: () -> Unit) {
    var scores   by remember { mutableStateOf<List<ScoredResult>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val computed = GameResultStore.results.map { result ->
            ScoredResult(result, detectFace(result))
        }
        scores    = computed
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(Color(0xFF1A1A1F)),
            contentAlignment = Alignment.Center
        ) {
            Text("RESULTS", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF3885F0))
                    Spacer(Modifier.height(12.dp))
                    Text("Analysing your snaps…", color = Color(0xFF8C8C91), fontSize = 13.sp)
                }
            }
        } else {
            val list   = scores ?: emptyList()
            val avg    = if (list.isEmpty()) 0f else list.map { it.score }.average().toFloat()
            val (grade, gradeColor) = overallGrade(avg)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(120.dp)
                        .background(gradeColor.copy(alpha = 0.12f), RoundedCornerShape(60.dp))
                        .border(3.dp, gradeColor, RoundedCornerShape(60.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(grade, fontSize = 64.sp, fontWeight = FontWeight.Bold, color = gradeColor)
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "${(avg * 100).roundToInt()}% overall",
                    color = Color(0xFF8C8C91),
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(24.dp))

                
                list.forEach { scored ->
                    ExpressionResultRow(scored)
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .padding(bottom = 24.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3885F0))
            ) {
                Text("PLAY AGAIN", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun ExpressionResultRow(scored: ScoredResult) {
    val pct         = (scored.score * 100).roundToInt()
    val barColor    = when {
        pct >= 80 -> Color(0xFF26AA3F)
        pct >= 60 -> Color(0xFF3885F0)
        pct >= 40 -> Color(0xFFFAD12E)
        else      -> Color(0xFFEF4444)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFFDBDBE0), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter            = painterResource(id = scored.result.drawableId),
            contentDescription = scored.result.expressionName,
            modifier           = Modifier.size(44.dp)
        )

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = "#${scored.result.sequenceIndex}  ${scored.result.expressionName}",
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF1A1A1F)
                )
                Text(
                    text      = "$pct%",
                    fontSize  = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color     = barColor
                )
            }
            Spacer(Modifier.height(5.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(Color(0xFFDBDBE0), RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(scored.score.coerceIn(0f, 1f))
                        .height(5.dp)
                        .background(barColor, RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

package com.group3.camera

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

enum class EmoticonExpression {
    NEUTRAL, SMILE, WINK, LOOK_LEFT, LOOK_RIGHT, LOOK_UP, LOOK_DOWN
}

@Composable
fun EmoticonFace(
    expression: EmoticonExpression = EmoticonExpression.NEUTRAL,
    color: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = (w * 0.075f).coerceAtLeast(4f)
        val half = stroke / 2f

        // Outer square border
        drawRect(
            color = color,
            topLeft = Offset(half, half),
            size = Size(w - stroke, h - stroke),
            style = Stroke(width = stroke)
        )

        val eyeW = w * 0.10f
        val eyeH = h * 0.22f
        val eyeBaseY = h * 0.28f
        val leftEyeX = w * 0.29f
        val rightEyeX = w * 0.61f

        val eyeShiftX = when (expression) {
            EmoticonExpression.LOOK_LEFT -> -w * 0.07f
            EmoticonExpression.LOOK_RIGHT -> w * 0.07f
            else -> 0f
        }
        val eyeShiftY = when (expression) {
            EmoticonExpression.LOOK_UP -> -h * 0.07f
            EmoticonExpression.LOOK_DOWN -> h * 0.07f
            else -> 0f
        }

        // Left eye
        drawRect(
            color = color,
            topLeft = Offset(leftEyeX + eyeShiftX, eyeBaseY + eyeShiftY),
            size = Size(eyeW, eyeH)
        )

        // Right eye (diagonal slash for wink)
        if (expression == EmoticonExpression.WINK) {
            drawLine(
                color = color,
                start = Offset(rightEyeX, eyeBaseY + eyeH * 0.2f),
                end = Offset(rightEyeX + eyeW, eyeBaseY + eyeH * 0.8f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        } else {
            drawRect(
                color = color,
                topLeft = Offset(rightEyeX + eyeShiftX, eyeBaseY + eyeShiftY),
                size = Size(eyeW, eyeH)
            )
        }

        // Mouth
        val mouthY = h * 0.68f
        when (expression) {
            EmoticonExpression.SMILE -> {
                val path = Path().apply {
                    moveTo(w * 0.24f, mouthY)
                    quadraticTo(w * 0.5f, h * 0.84f, w * 0.76f, mouthY)
                }
                drawPath(path, color, style = Stroke(width = stroke * 0.85f, cap = StrokeCap.Round))
            }
            else -> {
                // Flat mouth bar
                drawRect(
                    color = color,
                    topLeft = Offset(w * 0.24f, mouthY),
                    size = Size(w * 0.52f, h * 0.055f)
                )
            }
        }
    }
}

package com.group3.microphone

data class Platform(
    val id: Int,
    val x: Float,                   // current visible left-edge (includes oscillation for moving platforms)
    val yFraction: Float,           // top edge as fraction of canvas height
    val widthFraction: Float,       // width as fraction of canvas width
    val scrollX: Float = x,         // left-edge due to scrolling only; base position for oscillation
    val moveAmplitude: Float = 0f,  // oscillation half-width (canvas-width fraction); 0 = static platform
    val moveSpeed: Float = 0f,      // oscillation angular speed (radians / second)
    val movePhase: Float = 0f       // current oscillation phase (radians)
)

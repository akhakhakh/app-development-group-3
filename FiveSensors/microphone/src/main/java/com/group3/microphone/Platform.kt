package com.group3.microphone

data class Platform(
    val id: Int,
    val x: Float,            // left edge as fraction of canvas width (moves left each frame)
    val yFraction: Float,    // top edge as fraction of canvas height (fixed)
    val widthFraction: Float // width as fraction of canvas width (fixed)
)

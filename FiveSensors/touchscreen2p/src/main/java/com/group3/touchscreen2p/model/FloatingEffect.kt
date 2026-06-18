package com.group3.touchscreen2p.model

import java.util.UUID

data class FloatingEffect(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val normalizedX: Float,
    val normalizedY: Float,
    val player: Int,
    val type: TargetType = TargetType.BULLSEYE,
    val startTimeMs: Long, // fading duration is calculated from start time

)

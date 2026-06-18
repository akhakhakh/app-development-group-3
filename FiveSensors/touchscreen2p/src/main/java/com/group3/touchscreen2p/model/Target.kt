package com.group3.touchscreen2p.model

import com.group3.touchscreen2p.Constants
import java.util.UUID

data class Target(
    val id: String = UUID.randomUUID().toString(),
    val player: Int,
    // random position, to calculate actual pixel in UI. actual pixel X/Y = normalizedX/Y × screen width/height
    val normalizedX: Float,
    val normalizedY: Float,
    val spawnTimeMs: Long,
    val lifetimeMs: Long = Constants.TARGET_LIFETIME_MS,
    val progress: Float = 1f,
    val type: TargetType = TargetType.BULLSEYE
)
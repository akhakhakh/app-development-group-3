package com.group3.microphone.detector

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold

const val DEFAULT_JUMP_THRESHOLD = 0.0002f
private const val JUMP_COOLDOWN_MS = 300L

private data class DetectorState(
    val wasAbove: Boolean = false,
    val lastJumpAt: Long = 0L,
    val shouldJump: Boolean = false,
    val jumpAmplitude: Float = 0f
)

class JumpDetector(
    val threshold: Float = DEFAULT_JUMP_THRESHOLD,
    private val cooldownMs: Long = JUMP_COOLDOWN_MS
) {
    // emits the amplitude that triggered the jump (use it to scale jump height)
    fun detectJumps(amplitudeFlow: Flow<Float>): Flow<Float> =
        amplitudeFlow
            .runningFold(DetectorState()) { state, amplitude ->
                val isAbove = amplitude >= threshold
                val now = System.currentTimeMillis()
                val cooldownPassed = (now - state.lastJumpAt) >= cooldownMs
                val shouldJump = isAbove && !state.wasAbove && cooldownPassed
                DetectorState(
                    wasAbove = isAbove,
                    lastJumpAt = if (shouldJump) now else state.lastJumpAt,
                    shouldJump = shouldJump,
                    jumpAmplitude = if (shouldJump) amplitude else state.jumpAmplitude
                )
            }
            .filter { it.shouldJump }
            .map { it.jumpAmplitude }
}

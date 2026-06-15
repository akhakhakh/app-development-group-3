package com.group3.microphone.detector

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

const val DEFAULT_JUMP_THRESHOLD = 0.004f
private const val JUMP_COOLDOWN_MS = 600L
// collect peak amplitude for this long after threshold crossing before emitting jump
private const val PEAK_WINDOW_MS = 120L

class JumpDetector(
    var threshold: Float = DEFAULT_JUMP_THRESHOLD,
    private val cooldownMs: Long = JUMP_COOLDOWN_MS,
    private val peakWindowMs: Long = PEAK_WINDOW_MS
) {
    fun detectJumps(amplitudeFlow: Flow<Float>): Flow<Float> = flow {
        var lastJumpAt = 0L
        var trackingPeak = false
        var peakAmplitude = 0f
        var peakWindowEnd = 0L

        amplitudeFlow.collect { amplitude ->
            val now = System.currentTimeMillis()

            if (trackingPeak)
            {
                if (now < peakWindowEnd)
                {
                    if (amplitude > peakAmplitude) peakAmplitude = amplitude
                } else {
                    // window closed — emit the tracked peak as the jump strength
                    emit(peakAmplitude)
                    lastJumpAt = now
                    trackingPeak = false
                    peakAmplitude = 0f
                }

            } else if (amplitude >= threshold && (now - lastJumpAt) >= cooldownMs)
            {
                // rising edge start peak tracking window
                trackingPeak = true
                peakAmplitude = amplitude
                peakWindowEnd = now + peakWindowMs
            }
        }
    }
}

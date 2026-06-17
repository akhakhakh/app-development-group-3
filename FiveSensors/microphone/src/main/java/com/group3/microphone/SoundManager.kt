package com.group3.microphone

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

object SoundManager {

    @Volatile private var enabled = true

    // ── Mic mute gate ─────────────────────────────────────────────────────────
    // Set whenever a sound effect fires so the jump detector ignores echo bleed.
    @Volatile private var muteUntilMs: Long = 0L
    val isMicMuted: Boolean get() = System.currentTimeMillis() < muteUntilMs

    private fun muteMic(durationMs: Int, trailMs: Int = 300) {
        val end = System.currentTimeMillis() + durationMs + trailMs
        if (end > muteUntilMs) muteUntilMs = end
    }

    // ── Enable / disable all audio ────────────────────────────────────────────
    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
        if (!enabled) stopHomeMelody()
    }

    // ── Short sound effects (fire-and-forget) ─────────────────────────────────
    fun playJump()     { muteMic(120); playAsync(startHz = 320f, endHz = 700f, durationMs = 120) }
    fun playLand()     { muteMic(90);  playAsync(startHz = 200f, endHz = 80f,  durationMs = 90) }
    fun playGameOver() { muteMic(500); playAsync(startHz = 450f, endHz = 120f, durationMs = 500) }

    // ── Home-screen melody (looping) ──────────────────────────────────────────
    // Each pair: frequency in Hz (0 = rest), duration in ms.
    private val MELODY = listOf(
        523f to 150,   // C5
        659f to 150,   // E5
        784f to 150,   // G5
        659f to 300,   // E5 (held)
        523f to 150,   // C5
        440f to 150,   // A4
        392f to 300,   // G4 (held)
        0f   to 220,   // rest
        523f to 150,   // C5
        587f to 150,   // D5
        659f to 150,   // E5
        784f to 300,   // G5 (held)
        659f to 150,   // E5
        587f to 150,   // D5
        523f to 380,   // C5 (long)
        0f   to 280    // rest
    )

    @Volatile private var melodyRunning = false
    private var melodyThread: Thread? = null

    fun startHomeMelody() {
        if (!enabled || melodyRunning) return
        melodyRunning = true
        melodyThread = Thread {
            while (melodyRunning && !Thread.currentThread().isInterrupted) {
                for ((freq, dur) in MELODY) {
                    if (!melodyRunning || Thread.currentThread().isInterrupted) break
                    try {
                        if (freq > 0f) playNoteSync(freq, dur, amplitude = 18000.0)
                        else           Thread.sleep(dur.toLong())
                    } catch (_: InterruptedException) {
                        Thread.currentThread().interrupt()
                        break
                    } catch (_: Exception) {
                        break
                    }
                }
            }
        }.also { it.isDaemon = true; it.start() }
    }

    fun stopHomeMelody() {
        melodyRunning = false
        melodyThread?.interrupt()
        melodyThread = null
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private fun playAsync(startHz: Float, endHz: Float, durationMs: Int) {
        if (!enabled) return
        Thread {
            try {
                playNoteSync(startHz, endHz, durationMs, amplitude = 26000.0)
            } catch (_: Exception) {}
        }.also { it.isDaemon = true; it.start() }
    }

    // Overload for SFX: frequency sweep from startHz to endHz.
    private fun playNoteSync(
        startHz: Float,
        endHz: Float,
        durationMs: Int,
        amplitude: Double
    ) {
        val sr = 44100
        val n  = sr * durationMs / 1000
        val buf = ShortArray(n)
        for (i in buf.indices) {
            val t        = i.toDouble() / sr
            val progress = i.toFloat() / n
            val freq     = (startHz + (endHz - startHz) * progress).toDouble()
            val env      = envelope(progress)
            buf[i] = (sin(2.0 * PI * freq * t) * amplitude * env).toInt().toShort()
        }
        renderAndPlay(buf, sr, durationMs)
    }

    // Overload for melody: single steady pitch.
    private fun playNoteSync(freqHz: Float, durationMs: Int, amplitude: Double) {
        val sr = 44100
        val n  = sr * durationMs / 1000
        val buf = ShortArray(n)
        for (i in buf.indices) {
            val t        = i.toDouble() / sr
            val progress = i.toFloat() / n
            val env      = envelope(progress)
            buf[i] = (sin(2.0 * PI * freqHz * t) * amplitude * env).toInt().toShort()
        }
        renderAndPlay(buf, sr, durationMs)
    }

    private fun envelope(progress: Float): Double = when {
        progress < 0.04f -> (progress / 0.04f).toDouble()
        progress > 0.80f -> ((1f - progress) / 0.20f).toDouble()
        else             -> 1.0
    }

    private fun renderAndPlay(buf: ShortArray, sampleRate: Int, durationMs: Int) {
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setTransferMode(AudioTrack.MODE_STATIC)
            .setBufferSizeInBytes(buf.size * 2)
            .build()
        track.write(buf, 0, buf.size)
        track.play()
        Thread.sleep(durationMs.toLong())
        track.stop()
        track.release()
    }
}

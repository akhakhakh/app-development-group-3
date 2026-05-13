package com.group3.microphone.audio

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlin.math.sqrt

private const val SAMPLE_RATE = 44100
private const val READ_BUFFER_SHORTS = 2048

class MicAudioRecorder {

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun amplitudeFlow(): Flow<Float> = flow {
        val minBuffer = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferBytes = maxOf(minBuffer, READ_BUFFER_SHORTS * 2)
        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferBytes
        )
        val buffer = ShortArray(READ_BUFFER_SHORTS)
        recorder.startRecording()
        try {
            while (currentCoroutineContext().isActive) {
                val samplesRead = recorder.read(buffer, 0, buffer.size)
                if (samplesRead > 0) {
                    val sumOfSquares = buffer.take(samplesRead).sumOf { it.toDouble() * it }
                    val rms = sqrt(sumOfSquares / samplesRead).toFloat()
                    emit((rms / Short.MAX_VALUE).coerceIn(0f, 1f))
                }
            }
        } finally {
            recorder.stop()
            recorder.release()
        }
    }.flowOn(Dispatchers.IO)
}

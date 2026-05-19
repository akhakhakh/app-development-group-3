package com.group3.microphone.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group3.microphone.audio.MicAudioRecorder
import com.group3.microphone.detector.JumpDetector
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

private const val JUMP_VISUAL_DURATION_MS = 250L

class GameViewModel : ViewModel() {

    private val recorder = MicAudioRecorder()
    private val detector = JumpDetector()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude

    // 0f = not jumping, >0f = jump strength (the amplitude that triggered the jump)
    private val _jumpAmplitude = MutableStateFlow(0f)
    val jumpAmplitude: StateFlow<Float> = _jumpAmplitude

    private var listeningJob: Job? = null

    @SuppressLint("MissingPermission")
    fun startListening() {
        if (_isListening.value) return
        _isListening.value = true

        listeningJob = viewModelScope.launch {
            val sharedAmplitude = recorder.amplitudeFlow()
                .shareIn(this, SharingStarted.Eagerly, replay = 0)

            launch {
                sharedAmplitude.collect { _amplitude.value = it }
            }

            detector.detectJumps(sharedAmplitude).collect { amplitude ->
                _jumpAmplitude.value = amplitude
                delay(JUMP_VISUAL_DURATION_MS)
                _jumpAmplitude.value = 0f
            }
        }
    }

    fun stopListening() {
        listeningJob?.cancel()
        listeningJob = null
        _isListening.value = false
        _amplitude.value = 0f
        _jumpAmplitude.value = 0f
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}

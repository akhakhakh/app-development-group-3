package com.group3.touchscreen2p.viewmodel

import androidx.lifecycle.ViewModel
import com.group3.touchscreen2p.model.GameState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var countdownJob: Job? = null
    private var gameLoopJob: Job? = null
    private var spawnJob: Job? = null
}
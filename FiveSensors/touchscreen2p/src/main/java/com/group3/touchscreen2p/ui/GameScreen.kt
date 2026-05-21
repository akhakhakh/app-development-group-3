package com.group3.touchscreen2p.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.group3.touchscreen2p.Constants
import com.group3.touchscreen2p.model.Phase
import com.group3.touchscreen2p.ui.theme.NavyBackground
import com.group3.touchscreen2p.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onGameOver: (winner: Int, score1: Int, score2: Int, bestCombo: Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startGame()
    }

    LaunchedEffect(state.phase) {
        if (state.phase == Phase.GAME_OVER) {
            onGameOver(state.winner, state.score1, state.score2, state.bestCombo)
        }
    }

    val density = LocalDensity.current
    //Canvas use pixels while Constants use dp. Local Density gives the current screen density to
    // convert to pixels
    val targetRadiusPx = with(density) {
        Constants.TARGET_RADIUS_DP.dp.toPx() }
    val stateRef = rememberUpdatedState(state)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Press) {
                            event.changes.forEach { change ->
                                val tapX = change.position.x
                                val tapY = change.position.y
                                val nx = tapX / size.width
                                val ny = tapY / size.height
                                val player = if (ny > 0.5f) 1 else 2

                                val hitTarget = stateRef.value.targets
                                    .filter { it.player == player }
                                    .minByOrNull { t ->
                                        val dx = tapX - t.normalizedX * size.width
                                        val dy = tapY - t.normalizedY * size.height
                                        dx * dx + dy * dy
                                    }
                                    ?.takeIf { t ->
                                        val dx = tapX - t.normalizedX * size.width
                                        val dy = tapY - t.normalizedY * size.height
                                        dx * dx + dy * dy <= targetRadiusPx * targetRadiusPx
                                    }

                                if (hitTarget != null) {
                                    viewModel.onTargetHit(player, hitTarget, nx, ny)
                                }
                            }

                        }
                    }
                }
            }
    )
}


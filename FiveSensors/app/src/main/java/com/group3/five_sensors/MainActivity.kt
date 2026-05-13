package com.group3.five_sensors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group3.five_sensors.ui.theme.FiveSensorsTheme
import com.group3.five_sensors.viewmodel.GameViewModel
import com.group3.microphone.permission.MicPermissionState
import com.group3.microphone.permission.rememberMicPermissionState

private enum class Screen { HOME, GAME }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FiveSensorsTheme {
                var screen by remember { mutableStateOf(Screen.HOME) }
                BackHandler(enabled = screen == Screen.GAME) { screen = Screen.HOME }
                when (screen) {
                    Screen.HOME -> HomeScreen(
                        onPlay = { screen = Screen.GAME },
                        onHowToPlay = { },
                        onSettings = { }
                    )
                    Screen.GAME -> MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(gameViewModel: GameViewModel = viewModel()) {
    val context = LocalContext.current
    val micPermissionState = rememberMicPermissionState(context)
    val amplitude by gameViewModel.amplitude.collectAsState()
    val jumpAmplitude by gameViewModel.jumpAmplitude.collectAsState()

    LaunchedEffect(Unit) {
        if (!micPermissionState.isGranted) {
            micPermissionState.requestPermission()
        }
    }

    LaunchedEffect(micPermissionState.isGranted) {
        if (micPermissionState.isGranted) {
            gameViewModel.startListening()
        }
    }

    DisposableEffect(Unit) {
        onDispose { gameViewModel.stopListening() }
    }

    MainScreenContent(
        micPermissionState = micPermissionState,
        amplitude = amplitude,
        jumpAmplitude = jumpAmplitude
    )
}

private const val JUMP_MIN_DP = 10f
private const val JUMP_MAX_DP = 60f
private const val JUMP_SCALE = 500000f

@Composable
fun MainScreenContent(
    micPermissionState: MicPermissionState,
    amplitude: Float = 0f,
    jumpAmplitude: Float = 0f
) {
    val jumpHeightDp = (jumpAmplitude * JUMP_SCALE).coerceIn(JUMP_MIN_DP, JUMP_MAX_DP)
    val characterOffset by animateDpAsState(
        targetValue = if (jumpAmplitude > 0f) (-jumpHeightDp).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "jump_offset"
    )

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .offset(y = characterOffset)
                    .background(
                        color = if (jumpAmplitude > 0f) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Volume: ${"%.4f".format(amplitude)}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LinearProgressIndicator(
                progress = { (amplitude * 10f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )

            if (micPermissionState.hasBeenDenied && !micPermissionState.isGranted) {
                Text(
                    text = "Microphone permission is required. Please grant it in Settings.",
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Listening")
@Composable
fun MainScreenPreviewListening() {
    FiveSensorsTheme {
        MainScreenContent(
            micPermissionState = MicPermissionState(isGranted = true, hasBeenDenied = false, requestPermission = {}),
            amplitude = 0.6f,
            jumpAmplitude = 0f
        )
    }
}

@Preview(showBackground = true, name = "Permission Denied")
@Composable
fun MainScreenPreviewDenied() {
    FiveSensorsTheme {
        MainScreenContent(
            micPermissionState = MicPermissionState(isGranted = false, hasBeenDenied = true, requestPermission = {})
        )
    }
}

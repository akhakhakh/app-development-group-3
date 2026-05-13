package com.group3.five_sensors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.group3.five_sensors.ui.theme.FiveSensorsTheme
import com.group3.microphone.permission.rememberMicPermissionState

import androidx.compose.ui.tooling.preview.Preview
import com.group3.microphone.permission.MicPermissionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FiveSensorsTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val micPermissionState = rememberMicPermissionState(context)

    // Automatically requests on first launch
    LaunchedEffect(Unit) {
        if (!micPermissionState.isGranted) {
            micPermissionState.requestPermission()
        }
    }

    MainScreenContent(micPermissionState = micPermissionState)
}

@Preview(showBackground = true, name = "Permission Granted")
@Composable
fun MainScreenPreviewGranted() {
    FiveSensorsTheme {
        MainScreenContent(
            micPermissionState = MicPermissionState(
                isGranted = true,
                hasBeenDenied = false,
                requestPermission = {}
            )
        )
    }
}

@Preview(showBackground = true, name = "Permission Denied")
@Composable
fun MainScreenPreviewDenied() {
    FiveSensorsTheme {
        MainScreenContent(
            micPermissionState = MicPermissionState(
                isGranted = false,
                hasBeenDenied = true,
                requestPermission = {}
            )
        )
    }
}

@Composable
fun MainScreenContent(micPermissionState: MicPermissionState) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to Five Sensors",
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (micPermissionState.hasBeenDenied && !micPermissionState.isGranted) {
                Text(
                    text = "Microphone access is required to play 'Scream Go Hero'. Please grant the permission to enable the Play button.",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { },
                enabled = micPermissionState.isGranted
            ) {
                Text("Play")
            }
        }
    }
}

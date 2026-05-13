package com.group3.microphone.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat

@Composable
fun rememberMicPermissionState(context: Context): MicPermissionState {
    var isGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasBeenDenied by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted = granted
        hasBeenDenied = !granted
    }

    return remember(isGranted, hasBeenDenied) {
        MicPermissionState(
            isGranted = isGranted,
            hasBeenDenied = hasBeenDenied,
            requestPermission = { launcher.launch(Manifest.permission.RECORD_AUDIO) }
        )
    }
}

class MicPermissionState(
    val isGranted: Boolean,
    val hasBeenDenied: Boolean,
    val requestPermission: () -> Unit
)

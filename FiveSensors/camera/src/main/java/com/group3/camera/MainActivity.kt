package com.group3.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.group3.camera.ui.theme.FiveSensorsTheme

private sealed class Screen {
    object Landing : Screen()
    object HowToPlay : Screen()
    object FaceCheck : Screen()
    object ExpressionTutorial : Screen()
    object Game : Screen()
    object Results : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()
        setContent {
            FiveSensorsTheme {
                CameraPermissionWrapper {
                    var screen by remember { mutableStateOf<Screen>(Screen.Landing) }
                    BackHandler(enabled = screen != Screen.Landing) {
                        screen = Screen.Landing
                    }
                    when (screen) {
                        Screen.Landing ->
                            LandingScreen(
                                onPlay = { screen = Screen.FaceCheck },
                                onHowToPlay = { screen = Screen.HowToPlay }
                            )
                        Screen.HowToPlay ->
                            HowToPlayScreen(onBack = { screen = Screen.Landing })
                        Screen.FaceCheck ->
                            FaceCheckScreen(onNext = { screen = Screen.ExpressionTutorial })
                        Screen.ExpressionTutorial ->
                            ExpressionTutorialScreen(onNext = { screen = Screen.Game })
                        Screen.Game ->
                            GameScreen(onGameEnd = { screen = Screen.Results })
                        Screen.Results ->
                            GameResultScreen(onPlayAgain = { screen = Screen.Landing })
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

@Composable
private fun CameraPermissionWrapper(content: @Composable () -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(Manifest.permission.CAMERA)
    }

    if (hasPermission) {
        content()
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission is required to run this game.")
        }
    }
}

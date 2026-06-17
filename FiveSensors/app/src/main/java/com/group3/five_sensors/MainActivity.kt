package com.group3.five_sensors

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.group3.touchscreen2p.MainActivity as TapBattleActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

sealed class AppScreen {
    object Splash : AppScreen()
    object Intro : AppScreen()
    object GameList : AppScreen()
    data class GameDetail(val game: GameInfo) : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AppNavigation(onLaunchGame = { game ->
                    startActivity(Intent(this@MainActivity, game.activityClass))
                })
            }
        }

        binding.btnCameraDemo.setOnClickListener {
            startActivity(Intent(this, com.group3.camera.MainActivity::class.java))
        }
    }
}

@Composable
fun AppNavigation(onLaunchGame: (GameInfo) -> Unit) {
    var screen: AppScreen by remember { mutableStateOf(AppScreen.Splash) }

    when (val s = screen) {
        is AppScreen.Splash -> SplashScreen(onStart = { screen = AppScreen.Intro })
        is AppScreen.Intro -> IntroScreen(onGetStarted = { screen = AppScreen.GameList })
        is AppScreen.GameList -> GameListScreen(
            onGameSelected = { game -> screen = AppScreen.GameDetail(game) }
        )
        is AppScreen.GameDetail -> GameDetailScreen(
            game = s.game,
            onBack = { screen = AppScreen.GameList },
            onPlay = { onLaunchGame(s.game) }
        )
    }
}

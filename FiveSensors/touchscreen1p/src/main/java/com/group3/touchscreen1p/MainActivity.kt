package com.group3.touchscreen1p

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val playButton = findViewById<ImageButton>(R.id.playButton)
        val settingsButton = findViewById<LinearLayout>(R.id.settingsButton)
        val highScoreButton = findViewById<LinearLayout>(R.id.highScoreButton)
        val howToPlayButton = findViewById<LinearLayout>(R.id.howToPlayButton)

        playButton.setOnClickListener {
            Toast.makeText(this, "Start Game", Toast.LENGTH_SHORT).show()
        }

        settingsButton.setOnClickListener {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
        }

        highScoreButton.setOnClickListener {
            Toast.makeText(this, "High Score", Toast.LENGTH_SHORT).show()
        }

        howToPlayButton.setOnClickListener {
            Toast.makeText(this, "How To Play", Toast.LENGTH_SHORT).show()
        }
    }
}
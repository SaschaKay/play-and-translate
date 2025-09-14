package com.playandtranslate.wordsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.playandtranslate.wordsearch.ui.screens.GameScreen
import com.playandtranslate.wordsearch.ui.theme.PlayAndTranslateTheme

/**
 * Single-activity Compose app.
 * - Edge-to-edge enabled
 * - Material 3 theme
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Opt into edge-to-edge; padding is handled in Composables (you already use .padding)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PlayAndTranslateTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GameScreen()
                }
            }
        }
    }
}
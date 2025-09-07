package com.playandtranslate.wordsearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.playandtranslate.wordsearch.di.ServiceLocator
import com.playandtranslate.wordsearch.ui.screens.GameScreen
import com.playandtranslate.wordsearch.ui.theme.PlayAndTranslateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // init our tiny DI
        ServiceLocator.init(applicationContext)

        setContent {
            PlayAndTranslateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
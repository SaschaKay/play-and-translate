package com.playandtranslate.wordsearch.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.playandtranslate.wordsearch.ui.GameUiState
import com.playandtranslate.wordsearch.ui.GameViewModel

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    vm: GameViewModel = viewModel()
) {
    val state: GameUiState = vm.uiState.value

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        when {
            state.isLoading -> Text("Loading…")
            state.error != null -> Text("Error: ${state.error}")
            else -> {
                Text(
                    text = state.packTitle,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.padding(8.dp))

                // Word list: tap to reveal translation
                state.words.forEach { (de, en) ->
                    val show = remember(de) { mutableStateOf(false) }
                    Text(
                        text = if (show.value) "$de — $en" else de,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { show.value = !show.value }
                    )
                }
            }
        }
    }
}
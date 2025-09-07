package com.playandtranslate.wordsearch.ui.screens

@Composable
fun GameScreen(
    vm: GameViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state = vm.uiState.value
    when {
        state.isLoading -> Text("Loading…")
        state.error != null -> Text("Error: ${state.error}")
        else -> {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                androidx.compose.material3.Text(
                    text = state.packTitle,
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
                )
                androidx.compose.foundation.layout.Spacer(Modifier.padding(8.dp))
                // Word list: tap to reveal translation
                state.words.forEach { (de, en) ->
                    var show by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                    androidx.compose.material3.Text(
                        text = if (show) "$de — $en" else de,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { show = !show }
                    )
                }
            }
        }
    }
}
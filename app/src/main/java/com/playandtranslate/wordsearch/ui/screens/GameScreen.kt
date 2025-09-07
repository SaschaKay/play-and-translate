package com.playandtranslate.wordsearch.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel

import com.playandtranslate.wordsearch.ui.GameViewModel
import com.playandtranslate.wordsearch.domain.Cell
import com.playandtranslate.wordsearch.domain.Grid

private val CellSize: Dp = 40.dp
private const val LetterScale = 0.55f
private val CellRadius = 1.dp

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    vm: GameViewModel = viewModel()
) {
    val state = vm.uiState.value

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        when {
            state.isLoading -> Text("Loading…")
            state.error != null -> Text("Error: ${state.error}")
            else -> {
                Text(
                    text = state.packTitle,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(Modifier.height(12.dp))
                WordGrid(state.grid)
            }
        }
    }
}

@Composable
fun WordGrid(
    grid: Grid,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        grid.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { cell ->
                    GridCell(cell = cell)
                }
            }
        }
    }
}

@Composable
fun GridCell(
    modifier: Modifier = Modifier,
    cell: Cell
) {
    Box(
        modifier = modifier.size(CellSize),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = cell.letter.uppercaseChar().toString(),
            // keep proportions when you change CellSize
            fontSize = (CellSize.value * LetterScale).sp,
            // cleaner vertical centering
            style = MaterialTheme.typography.titleMedium.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                fontFamily = FontFamily.Monospace
            ),
            textAlign = TextAlign.Center
        )
    }
}
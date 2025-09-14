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
                WordGrid(
                    grid = state.grid,
                    selectedStart = state.selectedStart,
                    onCellTap = { r, c -> vm.onCellTap(r, c) }
                )

                Spacer(Modifier.height(16.dp))
                DebugWordList(
                    placements = state.placements,
                    skipped = state.skipped
                )

            }
        }
    }
}

@Composable
fun WordGrid(
    grid: Grid,
    modifier: Modifier = Modifier,
    onCellTap: (row: Int, col: Int) -> Unit = { _, _ -> },
    selectedStart: Pos? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        grid.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { cell ->
                    val isStart = selectedStart?.let { it.row == cell.row && it.col == cell.col } == true
                    GridCell(
                        cell = cell,
                        modifier = Modifier
                            .clickable { onCellTap(cell.row, cell.col) }
                            .then(
                                if (isStart) Modifier.background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                                    RoundedCornerShape(CellRadius)
                                ) else Modifier
                            )
                    )
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
        val style = if (cell.isFound) {
            MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        } else {
            MaterialTheme.typography.titleMedium
        }
            Text(
                text = cell.letter.uppercaseChar().toString(),
                fontSize = (CellSize.value * 0.55f).sp,
                style = style.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    fontFamily = FontFamily.Monospace
                ),
                textAlign = TextAlign.Center
            )
    }
}


@Composable
private fun DebugWordList(
    placements: List<com.playandtranslate.wordsearch.domain.WordPlacement>,
    skipped: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Placed words (${placements.size}):",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.height(6.dp))
        placements.forEach { p ->
            val first = p.cells.firstOrNull()
            val last = p.cells.lastOrNull()
            Text(
                text = "• ${p.original} → ${p.normalized}  " +
                        "(len=${p.cells.size})  " +
                        "from (${first?.row},${first?.col}) to (${last?.row},${last?.col})",
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (skipped.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Skipped (${skipped.size}): ${skipped.joinToString()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
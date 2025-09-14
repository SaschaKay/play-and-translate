package com.playandtranslate.wordsearch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.playandtranslate.wordsearch.domain.Cell
import com.playandtranslate.wordsearch.domain.Grid
import com.playandtranslate.wordsearch.domain.Pos
import com.playandtranslate.wordsearch.domain.WordPlacement
import com.playandtranslate.wordsearch.ui.GameViewModel

// Magic numbers -> constants
private val CellSize: Dp = 40.dp
private const val LetterScale = 0.55f
private val CellRadius = 6.dp
private val CellGap = 6.dp

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    vm: GameViewModel = viewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()          // <-- content avoids the notch
            .padding(16.dp)
    ) {
        when {
            state.isLoading -> Text("Loading…")
            state.error != null -> Text("Error: ${state.error}")
            else -> {
                Text(text = state.packTitle, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))

                WordGrid(
                    grid = state.grid,
                    selectedStart = state.selectedStart,
                    onCellTap = { r, c -> vm.onCellTap(r, c) },
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
private fun WordGrid(
    grid: Grid,
    modifier: Modifier = Modifier,
    selectedStart: Pos? = null,
    onCellTap: (row: Int, col: Int) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(CellGap)
    ) {
        grid.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(CellGap)) {
                row.forEach { cell ->
                    val isStart = selectedStart?.let { it.row == cell.row && it.col == cell.col } == true
                    GridCell(
                        cell = cell,
                        modifier = Modifier
                            .clickable { onCellTap(cell.row, cell.col) }
                            .then(
                                if (isStart) {
                                    Modifier.background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                                        RoundedCornerShape(CellRadius)
                                    )
                                } else {
                                    Modifier
                                }
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun GridCell(
    modifier: Modifier = Modifier,
    cell: Cell
) {
    Box(
        modifier = modifier.size(CellSize),
        contentAlignment = Alignment.Center
    ) {
        val base = MaterialTheme.typography.titleMedium
        val style = if (cell.isFound) base.copy(fontWeight = FontWeight.SemiBold) else base

        Text(
            text = cell.letter.uppercaseChar().toString(),
            fontSize = (CellSize.value * LetterScale).sp,
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
    placements: List<WordPlacement>,
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
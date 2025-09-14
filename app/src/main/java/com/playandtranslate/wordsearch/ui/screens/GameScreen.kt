package com.playandtranslate.wordsearch.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextDecoration
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

// Sizing constants (easy to tweak later)
private val CellSize: Dp = 40.dp
private const val LetterScale = 0.55f
private val CellRadius = 6.dp
private val CellGap = 6.dp
private val SelectionBorder = 2.dp

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    vm: GameViewModel = viewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val allFound = state.found.size == state.placements.size && state.placements.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        when {
            state.isLoading -> Text("Loading…")
            state.error != null -> Text("Error: ${state.error}")
            else -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.packTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = { vm.giveHint() },
                        enabled = !allFound,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text("Hint")
                    }
                }

                Spacer(Modifier.height(12.dp))

                WordGrid(
                    grid = state.grid,
                    selectedStart = state.selectedStart,
                    onCellTap = { r, c -> vm.onCellTap(r, c) },
                )

                Spacer(Modifier.height(16.dp))
                DebugWordList(
                    placements = state.placements,
                    found = state.found,
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
                                    Modifier.border(
                                        BorderStroke(
                                            SelectionBorder,
                                            MaterialTheme.colorScheme.secondary
                                        ),
                                        RoundedCornerShape(CellRadius)
                                    )
                                } else Modifier
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
    val foundBg = MaterialTheme.colorScheme.primaryContainer
    val foundFg = MaterialTheme.colorScheme.onPrimaryContainer
    val hintBg = MaterialTheme.colorScheme.secondaryContainer
    val hintFg = MaterialTheme.colorScheme.onSecondaryContainer

    Box(
        modifier = modifier
            .size(CellSize)
            .then(
                when {
                    cell.isFound -> Modifier.background(foundBg, RoundedCornerShape(CellRadius))
                    cell.isHint -> Modifier.background(hintBg, RoundedCornerShape(CellRadius))
                    else -> Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        val base = MaterialTheme.typography.titleMedium
        val style = when {
            cell.isFound -> base.copy(fontWeight = FontWeight.Bold)
            else -> base
        }
        val color = when {
            cell.isFound -> foundFg
            cell.isHint -> hintFg
            else -> MaterialTheme.colorScheme.onSurface
        }

        Text(
            text = cell.letter.uppercaseChar().toString(),
            fontSize = (CellSize.value * LetterScale).sp,
            style = style.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                fontFamily = FontFamily.Monospace,
                color = color
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DebugWordList(
    placements: List<WordPlacement>,
    found: Set<Int>,
    skipped: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Placed words (${placements.size}):",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.height(6.dp))
        placements.forEachIndexed { index, p ->
            val first = p.cells.firstOrNull()
            val last = p.cells.lastOrNull()
            val isFound = index in found
            val base = MaterialTheme.typography.bodySmall
            val style = if (isFound) {
                base.copy(textDecoration = TextDecoration.LineThrough)
            } else base

            Text(
                text = "${if (isFound) "✓" else "•"} ${p.original} → ${p.normalized}  " +
                        "(len=${p.cells.size})  " +
                        "from (${first?.row},${first?.col}) to (${last?.row},${last?.col})",
                style = style,
                color = if (isFound) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.onSurface
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
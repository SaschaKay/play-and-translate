package com.playandtranslate.wordsearch.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playandtranslate.wordsearch.data.PackRepository
import com.playandtranslate.wordsearch.di.ServiceLocator
import com.playandtranslate.wordsearch.domain.*
import kotlinx.coroutines.launch

// --- GameUiState: add selection + found words ---
data class GameUiState(
    val isLoading: Boolean = true,
    val packTitle: String = "",
    val grid: Grid = emptyList(),
    val placements: List<WordPlacement> = emptyList(),
    val skipped: List<String> = emptyList(),
    val selectedStart: Pos? = null,                 // first tap
    val foundWordIndexes: Set<Int> = emptySet(),    // which placements are found
    val error: String? = null
)

fun onCellTap(row: Int, col: Int) {
    val start = uiState.value.selectedStart
    if (start == null) {
        // first tap
        uiState.value = uiState.value.copy(selectedStart = Pos(row, col))
        return
    }
    // second tap: try to complete a straight-line selection
    val path = straightLine(start, Pos(row, col)) ?: run {
        // not straight → cancel selection
        uiState.value = uiState.value.copy(selectedStart = null)
        return
    }

    // does this path match any placement (forward or reverse)?
    val matchedIndex = uiState.value.placements.indexOfFirst { p ->
        pathsEqual(path, p.cells) || pathsEqual(path, p.cells.asReversed())
    }

    if (matchedIndex >= 0) {
        markWordFound(matchedIndex)
    }

    // clear selection either way
    uiState.value = uiState.value.copy(selectedStart = null)
}

private fun pathsEqual(a: List<Pos>, b: List<Pos>): Boolean {
    if (a.size != b.size) return false
    for (i in a.indices) if (a[i] != b[i]) return false
    return true
}

private fun markWordFound(index: Int) {
    val state = uiState.value
    if (index in state.foundWordIndexes) return // already found

    val newFound = state.foundWordIndexes + index
    // highlight cells of found word(s)
    val highlight = state.placements[index].cells.toHashSet()

    val newGrid: Grid = state.grid.map { row ->
        row.map { cell ->
            if (cell.posIn(highlight)) cell.copy(isFound = true) else cell
        }
    }

    uiState.value = state.copy(
        grid = newGrid,
        foundWordIndexes = newFound
    )
}

// tiny helper to check membership by coordinates
private fun Cell.posIn(set: Set<Pos>): Boolean = Pos(row, col) in set

class GameViewModel(
    private val repo: PackRepository = ServiceLocator.packRepository,
    private val gridGen: GenerateGrid = ServiceLocator.generateGrid
) : ViewModel() {

    val uiState = mutableStateOf(GameUiState())

    init { loadDefaultPack() }

    private fun loadDefaultPack() = viewModelScope.launch {
        try {
            val packId = repo.listBuiltinPacks().first()
            val pack = repo.loadPack(packId)

            val build = gridGen.createGrid(
                words = pack.words.map { it.source },
                size = 8,
                diagonals = true,
                allowIntersections = true
            )

            val cellsGrid: Grid = build.grid.mapIndexed { r, row ->
                row.mapIndexed { c, ch -> Cell(r, c, ch) }
            }

            uiState.value = GameUiState(
                isLoading = false,
                packTitle = pack.title,
                grid = cellsGrid,
                placements = build.placements,
                skipped = build.skipped
            )
        } catch (t: Throwable) {
            uiState.value = GameUiState(isLoading = false, error = t.message)
        }
    }
}
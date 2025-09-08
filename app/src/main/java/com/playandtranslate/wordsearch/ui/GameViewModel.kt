package com.playandtranslate.wordsearch.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playandtranslate.wordsearch.data.PackRepository
import com.playandtranslate.wordsearch.di.ServiceLocator
import com.playandtranslate.wordsearch.domain.*
import kotlinx.coroutines.launch

data class GameUiState(
    val isLoading: Boolean = true,
    val packTitle: String = "",
    val grid: Grid = emptyList(),
    val placements: List<WordPlacement> = emptyList(),
    val skipped: List<String> = emptyList(),
    val error: String? = null
)

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
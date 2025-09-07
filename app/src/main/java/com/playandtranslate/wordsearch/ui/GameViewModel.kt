package com.playandtranslate.wordsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playandtranslate.wordsearch.data.PackRepository
import com.playandtranslate.wordsearch.di.ServiceLocator
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import com.playandtranslate.wordsearch.domain.GenerateGrid
import com.playandtranslate.wordsearch.domain.Cell
import com.playandtranslate.wordsearch.domain.Grid

data class GameUiState(
    val isLoading: Boolean = true,
    val packTitle: String = "",
    val grid: Grid = emptyList(),
    val error: String? = null
)

class GameViewModel(
    private val repo: PackRepository = ServiceLocator.packRepository,
    private val gridGen: GenerateGrid = ServiceLocator.generateGrid
) : ViewModel() {

    val uiState = mutableStateOf(GameUiState())

    init {
        loadDefaultPack()
    }

    private fun loadDefaultPack() = viewModelScope.launch {
        try {
            val packId = repo.listBuiltinPacks().first()
            val pack = repo.loadPack(packId)
            val chars = gridGen.createGrid(words = pack.words.map { it.source }, size = 8)
            val cells: Grid = chars.mapIndexed { r, row ->
                row.mapIndexed { c, ch -> Cell(r, c, ch) }
            }

            uiState.value = GameUiState(
                isLoading = false,
                packTitle = pack.title,
                grid = cells
            )
        } catch (t: Throwable) {
            uiState.value = GameUiState(isLoading = false, error = t.message)
        }
    }
}
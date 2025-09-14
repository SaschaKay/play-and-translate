package com.playandtranslate.wordsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playandtranslate.wordsearch.di.ServiceLocator
import com.playandtranslate.wordsearch.domain.Cell
import com.playandtranslate.wordsearch.domain.Grid
import com.playandtranslate.wordsearch.domain.Pos
import com.playandtranslate.wordsearch.data.PackRepository
import com.playandtranslate.wordsearch.words.GenerateGrid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Minimal VM for the word-search MVP.
 *
 * - Repo does IO internally; generator runs on Dispatchers.Default.
 * - UI reads a single immutable GameUiState.
 */
class GameViewModel(
    private val repo: PackRepository = ServiceLocator.packRepository,
    private val gridGen: GenerateGrid = ServiceLocator.generateGrid
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadDefaultPack()
    }

    private fun setLoading() {
        _uiState.value = GameUiState(isLoading = true)
    }

    private fun setError(message: String?) {
        _uiState.value = GameUiState(isLoading = false, error = message ?: "Unknown error")
    }

    private fun update(transform: (GameUiState) -> GameUiState) {
        _uiState.value = transform(_uiState.value)
    }

    private fun loadDefaultPack() {
        setLoading()
        viewModelScope.launch {
            try {
                // Packs from assets (repo switches to IO internally)
                val packId = repo.listBuiltinPacks().firstOrNull()
                if (packId == null) {
                    setError("No built-in packs found in assets/packs.")
                    return@launch
                }
                val pack = repo.loadPack(packId)

                // Grid generation is CPU-ish → Default
                val build = withContext(Dispatchers.Default) {
                    gridGen.createGrid(
                        words = pack.words.map { it.source },
                        size = 8,
                        diagonals = true,            // MVP: allow; later turn off when smarter
                        allowIntersections = true    // MVP: allow
                    )
                }

                val cellsGrid: Grid = build.grid.mapIndexed { r, row ->
                    row.mapIndexed { c, ch -> Cell(r, c, ch) }
                }

                update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        packTitle = pack.title,
                        grid = cellsGrid,
                        placements = build.placements,
                        skipped = build.skipped
                    )
                }
            } catch (t: Throwable) {
                setError(t.message)
            }
        }
    }

    /** Two-tap MVP selection (start → end → clear). */
    fun onCellTap(row: Int, col: Int) {
        val pos = Pos(row, col)
        val start = _uiState.value.selectedStart
        if (start == null) {
            update { it.copy(selectedStart = pos) }
        } else {
            // Later: validate straight line, check match, mark found.
            update { it.copy(selectedStart = null) }
        }
    }
}
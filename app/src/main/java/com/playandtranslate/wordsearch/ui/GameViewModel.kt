package com.playandtranslate.wordsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playandtranslate.wordsearch.di.ServiceLocator
import com.playandtranslate.wordsearch.domain.Cell
import com.playandtranslate.wordsearch.domain.Grid
import com.playandtranslate.wordsearch.domain.Pos
import com.playandtranslate.wordsearch.data.PackRepository
import com.playandtranslate.wordsearch.words.GenerateGrid
import com.playandtranslate.wordsearch.words.straightLine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Minimal VM for the word-search MVP.
 *
 * - Repo does IO internally; generator runs on [computationDispatcher].
 * - UI reads a single immutable GameUiState.
 */
class GameViewModel(
    private val repo: PackRepository = ServiceLocator.packRepository,
    private val gridGen: GenerateGrid = ServiceLocator.generateGrid,
    private val computationDispatcher: CoroutineDispatcher = Dispatchers.Default
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
                val packId = repo.listBuiltinPacks().firstOrNull()
                if (packId == null) {
                    setError("No built-in packs found in assets/packs.")
                    return@launch
                }
                val pack = repo.loadPack(packId)

                val build = withContext(computationDispatcher) {
                    gridGen.createGrid(
                        words = pack.words.map { it.source },
                        size = 8,
                        diagonals = true,
                        allowIntersections = true
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
                        skipped = build.skipped,
                        found = emptySet(),
                        selectedStart = null,
                        hintsUsed = 0
                    )
                }
            } catch (t: Throwable) {
                setError(t.message)
            }
        }
    }

    /** Two-tap selection: start → end. If path matches any placement (forward/reverse), mark found. */
    fun onCellTap(row: Int, col: Int) {
        val end = Pos(row, col)
        val s = _uiState.value
        val start = s.selectedStart

        if (start == null) {
            update { it.copy(selectedStart = end) }
            return
        }

        val path = straightLine(start, end)
        if (path.isEmpty()) {
            update { it.copy(selectedStart = null) }
            return
        }

        val idx = s.placements.indexOfFirst { wp ->
            wp.cells == path || wp.cells.asReversed() == path
        }

        if (idx >= 0 && idx !in s.found) {
            val pathSet = path.toHashSet()
            val newGrid: Grid = s.grid.map { rowList ->
                rowList.map { cell ->
                    // found has precedence over hint
                    if (Pos(cell.row, cell.col) in pathSet) cell.copy(isFound = true, isHint = false)
                    else cell
                }
            }
            update {
                it.copy(
                    grid = newGrid,
                    found = it.found + idx,
                    selectedStart = null
                )
            }
        } else {
            update { it.copy(selectedStart = null) }
        }
    }

    /** Reveal one letter cell from the next unfound word. */
    fun giveHint() {
        val s = _uiState.value
        // no-op if loading/error or done
        if (s.isLoading || s.error != null) return
        val nextIdx = s.placements.indices.firstOrNull { it !in s.found } ?: return
        val wp = s.placements[nextIdx]

        // pick first cell of the word that isn't found and isn't already hinted
        val targetPos = wp.cells.firstOrNull { p ->
            val c = s.grid[p.row][p.col]
            !c.isFound && !c.isHint
        } ?: return // nothing to hint (all found/hinted)

        val newGrid: Grid = s.grid.map { row ->
            row.map { cell ->
                if (cell.row == targetPos.row && cell.col == targetPos.col && !cell.isFound) {
                    cell.copy(isHint = true)
                } else cell
            }
        }
        update {
            it.copy(
                grid = newGrid,
                hintsUsed = it.hintsUsed + 1
            )
        }
    }
}
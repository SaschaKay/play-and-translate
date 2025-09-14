package com.playandtranslate.wordsearch.ui

import com.playandtranslate.wordsearch.domain.Grid
import com.playandtranslate.wordsearch.domain.Pos
import com.playandtranslate.wordsearch.domain.WordPlacement

/**
 * Single source of truth for GameScreen.
 * Keep it immutable and simple. Default state = loading.
 */
data class GameUiState(
    val isLoading: Boolean = true,
    val error: String? = null,

    val packTitle: String = "",
    val grid: Grid = emptyList(),
    val placements: List<WordPlacement> = emptyList(),
    val skipped: List<String> = emptyList(),

    /** Indices of placements successfully found by the player. */
    val found: Set<Int> = emptySet(),

    // Interaction state
    val selectedStart: Pos? = null,
)
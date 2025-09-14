package com.playandtranslate.wordsearch.domain

/**
 * Result of the generator: final raw grid + placements + skipped words.
 *
 * Keep this Char-based so generation stays domain/pure and cheap.
 * The ViewModel decorates it into a [Grid] of [Cell]s for the UI layer.
 */
data class GridBuild(
    val grid: CharGrid,
    val placements: List<WordPlacement>,
    val skipped: List<String> = emptyList()
)
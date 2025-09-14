package com.playandtranslate.wordsearch.domain

/**
 * Where a word ended up on the board.
 *
 * @param original   Word as in the pack (e.g., "Wasser")
 * @param normalized Internal normalized form (e.g., "WASSER")
 * @param cells      The exact path the word occupies, in order
 */
data class WordPlacement(
    val original: String,
    val normalized: String,
    val cells: List<Pos>
)
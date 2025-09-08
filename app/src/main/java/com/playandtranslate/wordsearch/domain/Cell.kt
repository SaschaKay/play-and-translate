package com.playandtranslate.wordsearch.domain

data class Cell(
    val row: Int,
    val col: Int,
    val letter: Char,
    val isFound: Boolean = false,
    val isHint: Boolean = false,
)

typealias Grid = List<List<Cell>>
data class Pos(val row: Int, val col: Int)

/** Where a word ended up on the board. */
data class WordPlacement(
    val original: String,     // original input (e.g., "Wasser")
    val normalized: String,   // sanitized uppercase (e.g., "WASSER")
    val cells: List<Pos>      // positions in order
)

/** Result of the generator: final grid + all word placements. */
data class GridBuild(
    val grid: List<List<Char>>,
    val placements: List<WordPlacement>,
    val skipped: List<String> = emptyList() // words we couldn't place
)
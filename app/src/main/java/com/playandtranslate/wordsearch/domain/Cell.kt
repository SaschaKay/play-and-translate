package com.playandtranslate.wordsearch.domain

/**
 * A single immutable cell on the grid.
 *
 * UI can derive highlighting from [isFound] and [isHint].
 * Keep this class dumb and cheap—domain models should be tiny value objects.
 */
data class Cell(
    val row: Int,
    val col: Int,
    val letter: Char,
    val isFound: Boolean = false,
    val isHint: Boolean = false,
)
package com.playandtranslate.wordsearch.domain

data class Cell(
    val row: Int,
    val col: Int,
    val letter: Char,
    val isFound: Boolean = false,
    val isHint: Boolean = false,
)

typealias Grid = List<List<Cell>>
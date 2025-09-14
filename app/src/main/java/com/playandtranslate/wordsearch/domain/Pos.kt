package com.playandtranslate.wordsearch.domain

/**
 * A position on the grid.
 *
 * Comparable so we can sort or use < and > safely.
 * Uses row-major order: first by [row], then by [col].
 */
data class Pos(val row: Int, val col: Int) : Comparable<Pos> {
    override operator fun compareTo(other: Pos): Int =
        if (row == other.row) col - other.col else row - other.row
}
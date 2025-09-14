package com.playandtranslate.wordsearch.words

import com.playandtranslate.wordsearch.domain.Pos
import kotlin.math.abs

/**
 * Returns a straight line between two positions (inclusive),
 * if they are aligned horizontally, vertically, or diagonally.
 * Otherwise returns emptyList().
 *
 * Useful for selection logic (start→end path).
 */
fun straightLine(start: Pos, end: Pos): List<Pos> {
    val dr = end.row - start.row
    val dc = end.col - start.col

    val stepR = dr.sign()
    val stepC = dc.sign()

    // Not straight or diagonal
    if (!(dr == 0 || dc == 0 || abs(dr) == abs(dc))) return emptyList()

    val len = maxOf(abs(dr), abs(dc)) + 1
    return List(len) { i ->
        Pos(start.row + stepR * i, start.col + stepC * i)
    }
}

private fun Int.sign(): Int = when {
    this > 0 -> 1
    this < 0 -> -1
    else -> 0
}
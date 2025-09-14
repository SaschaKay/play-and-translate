package com.playandtranslate.wordsearch.domain

/** Returns all positions on a straight line from start to end (horiz/vert/diag), or null if not straight. */
fun straightLine(start: Pos, end: Pos): List<Pos>? {
    val dr = end.row - start.row
    val dc = end.col - start.col
    if (dr == 0 && dc == 0) return listOf(start)

    val stepR = when {
        dr == 0 -> 0
        dr > 0  -> 1
        else    -> -1
    }
    val stepC = when {
        dc == 0 -> 0
        dc > 0  -> 1
        else    -> -1
    }

    // must be straight: horizontal, vertical, or diagonal (same abs slope)
    if (!(dr == 0 || dc == 0 || kotlin.math.abs(dr) == kotlin.math.abs(dc))) return null

    val len = maxOf(kotlin.math.abs(dr), kotlin.math.abs(dc)) + 1
    return List(len) { i -> Pos(start.row + i * stepR, start.col + i * stepC) }
}

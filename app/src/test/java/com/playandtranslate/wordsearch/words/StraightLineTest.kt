package com.playandtranslate.wordsearch.words

import com.playandtranslate.wordsearch.domain.Pos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StraightLineTest {

    @Test
    fun horizontal_line_is_returned_inclusive() {
        val path = straightLine(Pos(2, 1), Pos(2, 4))
        assertEquals(listOf(Pos(2,1), Pos(2,2), Pos(2,3), Pos(2,4)), path)
    }

    @Test
    fun vertical_line_is_returned_inclusive() {
        val path = straightLine(Pos(3, 5), Pos(0, 5))
        assertEquals(listOf(Pos(3,5), Pos(2,5), Pos(1,5), Pos(0,5)), path)
    }

    @Test
    fun diagonal_line_is_returned_inclusive() {
        val path = straightLine(Pos(1,1), Pos(4,4))
        assertEquals(listOf(Pos(1,1), Pos(2,2), Pos(3,3), Pos(4,4)), path)
    }

    @Test
    fun non_straight_returns_empty() {
        val path = straightLine(Pos(0,0), Pos(2,3))
        assertTrue(path.isEmpty())
    }
}
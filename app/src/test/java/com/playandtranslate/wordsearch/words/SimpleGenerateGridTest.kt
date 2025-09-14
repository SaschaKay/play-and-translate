package com.playandtranslate.wordsearch.words

import com.playandtranslate.wordsearch.domain.CharGrid
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class SimpleGenerateGridTest {

    @Test
    fun places_words_and_fills_letters() {
        val gen = SimpleGenerateGrid(Random(1234))
        val words = listOf("Wasser", "Apfel", "Brot")
        val size = 8

        val build = gen.createGrid(
            words = words,
            size = size,
            diagonals = true,
            allowIntersections = true
        )

        // grid size
        assertEquals(size, build.grid.size)
        assertEquals(size, build.grid.first().size)

        // placements correspond to letters in grid
        for (wp in build.placements) {
            val norm = wp.normalized
            assertEquals(norm.length, wp.cells.size)
            wp.cells.forEachIndexed { i, pos ->
                val ch = build.grid[pos.row][pos.col]
                assertEquals(norm[i], ch)
            }
        }

        // fill empties: no zeros
        assertTrue(build.grid.flatten().all { it != '\u0000' })
    }

    @Test
    fun disallow_intersections_blocks_conflicting_overlap() {
        val gen = SimpleGenerateGrid(Random(42))
        val words = listOf("ABCD", "AXXX") // second would only fit overlapping at A
        val size = 6

        val allow = gen.createGrid(words, size, diagonals = false, allowIntersections = true)
        val disallow = gen.createGrid(words, size, diagonals = false, allowIntersections = false)

        // With intersections, it's easier to place both -> placements >= disallow
        assertTrue(allow.placements.size >= disallow.placements.size)
    }
}
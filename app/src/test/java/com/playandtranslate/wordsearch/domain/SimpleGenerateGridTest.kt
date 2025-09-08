package com.playandtranslate.wordsearch.domain

import org.junit.Assert.*
import org.junit.Test

class SimpleGenerateGridTest {

    private val gen = SimpleGenerateGrid()

    @Test
    fun `grid has correct size`() {
        val size = 8
        val build = gen.createGrid(listOf("CAT"), size = size)
        assertEquals(size, build.grid.size)
        build.grid.forEach { row ->
            assertEquals(size, row.size)
        }
    }

    @Test
    fun `sanitizes words correctly`() {
        val build = gen.createGrid(listOf(" kitty-cat!! "), size = 8)
        val normalized = build.placements.map { it.normalized }
        assertTrue("KITTYCAT" in normalized)
    }

    @Test
    fun `all placed letters are uppercase`() {
        val build = gen.createGrid(listOf("dog"), size = 8)
        for (row in build.grid) {
            for (ch in row) {
                assertTrue(ch.isUpperCase())
            }
        }
    }

    @Test
    fun `all words appear in placements or skipped`() {
        val words = listOf("APPLE", "BANANA", "CHERRY")
        val build = gen.createGrid(words, size = 8)

        val placed = build.placements.map { it.original }
        val skipped = build.skipped

        for (w in words) {
            assertTrue(
                "$w should be either placed or skipped",
                w in placed || w in skipped
            )
        }
    }
}
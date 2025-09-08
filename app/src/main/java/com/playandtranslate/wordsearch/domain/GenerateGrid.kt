package com.playandtranslate.wordsearch.domain

import kotlin.random.Random

interface GenerateGrid {
    fun createGrid(
        words: List<String>,
        size: Int = 8,
        diagonals: Boolean = false,
        allowIntersections: Boolean = false
    ): GridBuild
}

class SimpleGenerateGrid : GenerateGrid {

    override fun createGrid(
        words: List<String>,
        size: Int,
        diagonals: Boolean,
        allowIntersections: Boolean
    ): GridBuild {
        val grid = Array(size) { CharArray(size) { '\u0000' } }

        val directions4 = listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)
        val directions8 = directions4 + listOf(1 to 1, 1 to -1, -1 to 1, -1 to -1)
        val directions = if (diagonals) directions8 else directions4

        val cleaned = words.map { it to sanitize(it) }
            .filter { it.second != null }
            .map { it.first to it.second!! }
            .sortedByDescending { it.second.length } // longest first

        val rnd = Random.Default
        val placements = mutableListOf<WordPlacement>()
        val skipped = mutableListOf<String>()

        cleaned.forEach { (original, normalized) ->
            var placed = false
            repeat(350) {
                if (placed) return@repeat
                val (dr, dc) = directions.random(rnd)
                val r0 = rnd.nextInt(size)
                val c0 = rnd.nextInt(size)
                if (canPlace(grid, normalized, r0, c0, dr, dc, allowIntersections)) {
                    val cells = place(grid, normalized, r0, c0, dr, dc)
                    placements += WordPlacement(
                        original = original,
                        normalized = normalized,
                        cells = cells
                    )
                    placed = true
                }
            }
            if (!placed) skipped += original
        }

        // fill remaining cells
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (grid[r][c] == '\u0000') grid[r][c] = ('A'..'Z').random(rnd)
            }
        }

        return GridBuild(
            grid = grid.map { it.toList() },
            placements = placements.toList(),
            skipped = skipped.toList()
        )
    }

    private fun sanitize(raw: String): String? {
        val filtered = raw.trim().uppercase().filter { it.isLetter() }
        return filtered.ifEmpty { null }
    }

    private fun canPlace(
        grid: Array<CharArray>,
        word: String,
        r0: Int,
        c0: Int,
        dr: Int,
        dc: Int,
        allowIntersections: Boolean
    ): Boolean {
        val n = grid.size
        var r = r0
        var c = c0
        for (ch in word) {
            if (r !in 0 until n || c !in 0 until n) return false
            val cur = grid[r][c]
            if (cur != '\u0000') {
                if (!allowIntersections) return false
                if (cur != ch) return false
            }
            r += dr
            c += dc
        }
        return true
    }

    /** Places and returns the list of positions used (in order). */
    private fun place(
        grid: Array<CharArray>,
        word: String,
        r0: Int,
        c0: Int,
        dr: Int,
        dc: Int
    ): List<Pos> {
        val used = ArrayList<Pos>(word.length)
        var r = r0
        var c = c0
        for (ch in word) {
            grid[r][c] = ch
            used += Pos(r, c)
            r += dr
            c += dc
        }
        return used
    }
}
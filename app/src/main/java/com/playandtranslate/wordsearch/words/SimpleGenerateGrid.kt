package com.playandtranslate.wordsearch.words

import com.playandtranslate.wordsearch.domain.CharGrid
import com.playandtranslate.wordsearch.domain.GridBuild
import com.playandtranslate.wordsearch.domain.Pos
import com.playandtranslate.wordsearch.domain.WordPlacement
import kotlin.math.abs
import kotlin.random.Random

/**
 * Very small word-placement algorithm for MVP.
 * - Places words in random directions
 * - Supports diagonals & intersections via flags
 * - Fills empty cells with random A..Z
 *
 * Not “smart” yet: later you can bias to fewer skips / nicer overlaps.
 */
class SimpleGenerateGrid(
    private val random: Random = Random.Default
) : GenerateGrid {

    override fun createGrid(
        words: List<String>,
        size: Int,
        diagonals: Boolean,
        allowIntersections: Boolean
    ): GridBuild {
        require(size >= 4) { "Grid size too small: $size" }

        // Nullable grid while placing; we fill empties later.
        val grid: Array<CharArray?> = Array(size) { null }
        for (r in 0 until size) grid[r] = CharArray(size) { EMPTY }

        val placements = mutableListOf<WordPlacement>()
        val skipped = mutableListOf<String>()

        val dirs = buildDirections(diagonals)
        val maxAttemptsPerWord = 200

        for (w in words) {
            val original = w
            val normalized = normalize(w)
            if (normalized.isEmpty()) continue

            var placed = false
            var attempts = 0

            while (!placed && attempts < maxAttemptsPerWord) {
                attempts++
                val (dx, dy) = dirs.random(random)

                // Random start that *could* fit (we test bounds below)
                val startRow = random.nextInt(0, size)
                val startCol = random.nextInt(0, size)

                if (!fits(startRow, startCol, dx, dy, normalized.length, size)) continue
                if (!canPlace(grid, startRow, startCol, dx, dy, normalized, allowIntersections)) continue

                // Place letters
                val cells = mutableListOf<Pos>()
                for (i in normalized.indices) {
                    val r = startRow + dy * i
                    val c = startCol + dx * i
                    grid[r]!![c] = normalized[i]
                    cells += Pos(r, c)
                }
                placements += WordPlacement(
                    original = original,
                    normalized = normalized,
                    cells = cells
                )
                placed = true
            }

            if (!placed) skipped += original
        }

        // Fill empties with random letters
        for (r in 0 until size) {
            val row = grid[r]!!
            for (c in 0 until size) {
                if (row[c] == EMPTY) row[c] = randomLetter()
            }
        }

        // Convert to CharGrid (immutable)
        val charGrid: CharGrid = List(size) { r ->
            List(size) { c -> grid[r]!![c] }
        }

        return GridBuild(
            grid = charGrid,
            placements = placements,
            skipped = skipped
        )
    }

    // --- helpers ---

    private fun normalize(w: String): String =
        w.trim().uppercase().replace(" ", "")

    private fun buildDirections(diagonals: Boolean): List<Pair<Int, Int>> {
        val ortho = listOf(
            1 to 0,   // →
            -1 to 0,  // ←
            0 to 1,   // ↓
            0 to -1   // ↑
        )
        val diag = listOf(
            1 to 1,    // ↘
            1 to -1,   // ↗
            -1 to 1,   // ↙
            -1 to -1   // ↖
        )
        return if (diagonals) ortho + diag else ortho
    }

    private fun fits(sr: Int, sc: Int, dx: Int, dy: Int, len: Int, size: Int): Boolean {
        val er = sr + dy * (len - 1)
        val ec = sc + dx * (len - 1)
        return er in 0 until size && ec in 0 until size
    }

    private fun canPlace(
        grid: Array<CharArray?>,
        sr: Int,
        sc: Int,
        dx: Int,
        dy: Int,
        word: String,
        allowIntersections: Boolean
    ): Boolean {
        for (i in word.indices) {
            val r = sr + dy * i
            val c = sc + dx * i
            val existing = grid[r]!![c]
            if (existing == EMPTY) continue
            if (!allowIntersections) return false
            if (existing != word[i]) return false
        }
        return true
    }

    private fun randomLetter(): Char = ('A'..'Z').random(random)

    private companion object {
        private const val EMPTY: Char = '\u0000'
    }
}
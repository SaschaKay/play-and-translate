package com.playandtranslate.wordsearch.words

import com.playandtranslate.wordsearch.domain.GridBuild

/**
 * Builds a character grid and records where words are placed.
 * Keep this logic UI-agnostic and deterministic given the Random.
 */
interface GenerateGrid {
    /**
     * @param words               words to place (arbitrary case)
     * @param size                square grid size (NxN)
     * @param diagonals           allow diagonal placements
     * @param allowIntersections  allow crossing existing letters (must match)
     */
    fun createGrid(
        words: List<String>,
        size: Int,
        diagonals: Boolean,
        allowIntersections: Boolean
    ): GridBuild
}
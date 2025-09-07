package com.playandtranslate.wordsearch.domain

interface GenerateGrid {
    fun createGrid(words: List<String>, size: Int = 8): List<List<Char>>
}

class SimpleGenerateGrid : GenerateGrid {
    override fun createGrid(words: List<String>, size: Int): List<List<Char>> {
        // TODO: implement placement next step; return dummy grid for now
        return List(size) { List(size) { ('A'..'Z').random() } }
    }
}
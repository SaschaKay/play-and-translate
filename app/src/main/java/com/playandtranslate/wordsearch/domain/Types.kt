package com.playandtranslate.wordsearch.domain

/**
 * A 2D immutable grid of UI cells.
 *
 * NOTE: Distinct from [CharGrid], which is a raw letters matrix
 * coming from the generator before decoration into [Cell]s.
 */
typealias Grid = List<List<Cell>>

/** Raw character grid returned by the generator. */
typealias CharGrid = List<List<Char>>
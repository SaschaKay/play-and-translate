package com.playandtranslate.wordsearch.data

import kotlinx.serialization.Serializable

@Serializable
data class WordPack(
    val packId: String,
    val version: Int,
    val sourceLang: String,
    val targetLang: String,
    val direction: String,
    val title: String,
    val origin: String,
    val words: List<WordEntry>
)

@Serializable
data class WordEntry(
    val source: String,
    val target: String
)
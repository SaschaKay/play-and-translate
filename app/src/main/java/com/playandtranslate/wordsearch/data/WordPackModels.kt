package com.playandtranslate.wordsearch.data

import kotlinx.serialization.Serializable

@Serializable
data class WordPack(
    val packId: String,
    val version: Int,
    val sourceLang: String,
    val targetLang: String,
    val direction: String, // "LTR" or "RTL"
    val title: String,
    val origin: String,    // "builtin" | "user"
    val words: List<WordEntry>
)

@Serializable
data class WordEntry(
    val source: String, // German
    val target: String  // English
)
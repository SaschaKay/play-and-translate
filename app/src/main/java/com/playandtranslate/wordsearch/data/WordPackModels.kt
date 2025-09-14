package com.playandtranslate.wordsearch.data

import kotlinx.serialization.Serializable

/**
 * Metadata + vocabulary list for a single built-in or user pack.
 *
 * Keep this schema stable; app size depends on small JSON.
 * You can evolve it with ignoreUnknownKeys=true in the decoder.
 */
@Serializable
data class WordPack(
    val packId: String,
    val version: Int = 1,

    val sourceLang: String,                 // e.g., "de"
    val targetLang: String,                 // e.g., "en"
    val direction: String = "LTR",          // support RTL later

    val title: String,                      // human-friendly
    val origin: String = "builtin",         // "builtin" | "user"

    val words: List<WordEntry>
)

/** A single term/translation pair. */
@Serializable
data class WordEntry(
    val source: String,                     // "Wasser"
    val target: String                      // "water"
)
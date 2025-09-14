package com.playandtranslate.wordsearch.data

import com.playandtranslate.wordsearch.data.WordPack

/**
 * Abstract source of word packs.
 * MVP uses Assets; later you can add Room (user packs) and switch the locator.
 */
interface PackRepository {

    /**
     * Lists built-in pack IDs (e.g., ["basics", "verbs"]).
     * These map to files like assets/packs/<packId>.json.
     */
    suspend fun listBuiltinPacks(): List<String>

    /**
     * Loads a single pack by its ID.
     */
    suspend fun loadPack(packId: String): WordPack
}
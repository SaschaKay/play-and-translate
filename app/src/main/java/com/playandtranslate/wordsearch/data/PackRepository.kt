package com.playandtranslate.wordsearch.data

interface PackRepository {
    suspend fun listBuiltinPacks(): List<String>  // e.g., ["basics"]
    suspend fun loadPack(packId: String): WordPack
}
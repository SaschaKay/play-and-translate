package com.playandtranslate.wordsearch.data

import android.content.res.AssetManager
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AssetsPackRepository(private val assets: AssetManager) : PackRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun listBuiltinPacks(): List<String> = listOf("basics")

    override suspend fun loadPack(packId: String): WordPack {
        val path = "packs/$packId.json"
        assets.open(path).use { inStream ->
            val text = inStream.reader().readText()
            return json.decodeFromString<WordPack>(text)
        }
    }
}
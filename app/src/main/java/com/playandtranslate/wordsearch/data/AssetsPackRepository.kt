package com.playandtranslate.wordsearch.data

import android.content.res.AssetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Asset-backed repository that loads JSON packs from src/main/assets/packs.
 * Keeps a tiny in-memory cache for repeated loads.
 */
class AssetsPackRepository(
    private val assets: AssetManager,
) : PackRepository {

    private val cache = mutableMapOf<String, WordPack>()

    override suspend fun listBuiltinPacks(): List<String> = withContext(Dispatchers.IO) {
        val names = assets.list(PACKS_DIR).orEmpty()
        names.asSequence()
            .filter { it.endsWith(".json", ignoreCase = true) }
            .map { it.removeSuffix(".json") }
            .sorted()
            .toList()
    }

    override suspend fun loadPack(packId: String): WordPack = withContext(Dispatchers.IO) {
        cache[packId] ?: run {
            val path = "$PACKS_DIR/$packId.json"
            val raw = assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
            val pack = WordPackJson.decode(raw)
            cache[packId] = pack
            pack
        }
    }

    private companion object {
        const val PACKS_DIR = "packs"
    }
}

/** Local JSON helper with safe config (ignores unknown keys). */
private object WordPackJson {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        prettyPrint = false
    }

    fun decode(text: String): WordPack = json.decodeFromString(text)
}
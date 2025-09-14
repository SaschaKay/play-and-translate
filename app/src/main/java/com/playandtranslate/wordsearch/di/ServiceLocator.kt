package com.playandtranslate.wordsearch.di

import android.content.Context
import com.playandtranslate.wordsearch.data.AssetsPackRepository
import com.playandtranslate.wordsearch.data.PackRepository
import com.playandtranslate.wordsearch.words.GenerateGrid
import com.playandtranslate.wordsearch.words.SimpleGenerateGrid
import kotlin.random.Random

/**
 * Tiny Service Locator for MVP:
 * - Provides a PackRepository backed by app assets
 * - Provides a simple grid generator
 */
object ServiceLocator {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val packRepository: PackRepository by lazy {
        check(::appContext.isInitialized) { "ServiceLocator not initialized. Call ServiceLocator.init(context) in Application." }
        AssetsPackRepository(appContext.assets)
    }

    val generateGrid: GenerateGrid by lazy {
        // Deterministic seed could help tests; using Default is fine for MVP.
        SimpleGenerateGrid(random = Random.Default)
    }
}
package com.playandtranslate.wordsearch.di

import android.content.Context
import com.playandtranslate.wordsearch.data.AssetsPackRepository
import com.playandtranslate.wordsearch.data.PackRepository
import com.playandtranslate.wordsearch.domain.GenerateGrid
import com.playandtranslate.wordsearch.domain.SimpleGenerateGrid

object ServiceLocator {
    // Late init to avoid context leaks
    lateinit var packRepository: PackRepository
        private set

    val generateGrid: GenerateGrid by lazy { SimpleGenerateGrid() }

    fun init(appContext: Context) {
        packRepository = AssetsPackRepository(appContext.assets)
    }
}
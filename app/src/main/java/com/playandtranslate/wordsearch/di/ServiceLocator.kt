package com.playandtranslate.wordsearch.di

object ServiceLocator {
    // Late init to avoid context leaks
    lateinit var packRepository: PackRepository
    val generateGrid: GenerateGrid by lazy { SimpleGenerateGrid() }

    fun init(appContext: android.content.Context) {
        packRepository = AssetsPackRepository(appContext.assets)
    }
}

ServiceLocator.init(applicationContext)
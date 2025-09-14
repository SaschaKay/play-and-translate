package com.playandtranslate.wordsearch

import android.app.Application
import com.playandtranslate.wordsearch.di.ServiceLocator

/**
 * Minimal Application just to initialize the ServiceLocator.
 * Keeps DI super light for the MVP.
 */
class WordSearchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
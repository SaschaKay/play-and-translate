package com.playandtranslate.wordsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playandtranslate.wordsearch.data.PackRepository
import com.playandtranslate.wordsearch.di.ServiceLocator
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf

data class GameUiState(
    val isLoading: Boolean = true,
    val packTitle: String = "",
    val words: List<Pair<String, String>> = emptyList(), // (de, en)
    val error: String? = null
)

class GameViewModel(
    private val repo: PackRepository = ServiceLocator.packRepository
) : ViewModel() {

    val uiState = mutableStateOf(GameUiState())

    init {
        loadDefaultPack()
    }

    private fun loadDefaultPack() = viewModelScope.launch {
        try {
            val packId = repo.listBuiltinPacks().first()
            val pack = repo.loadPack(packId)
            uiState.value = GameUiState(
                isLoading = false,
                packTitle = pack.title,
                words = pack.words.map { it.source to it.target }
            )
        } catch (t: Throwable) {
            uiState.value = GameUiState(isLoading = false, error = t.message)
        }
    }
}
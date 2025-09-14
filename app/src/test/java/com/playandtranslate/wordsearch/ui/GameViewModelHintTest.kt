package com.playandtranslate.wordsearch.ui

import com.playandtranslate.wordsearch.data.PackRepository
import com.playandtranslate.wordsearch.data.WordEntry
import com.playandtranslate.wordsearch.data.WordPack
import com.playandtranslate.wordsearch.testutil.MainDispatcherRule
import com.playandtranslate.wordsearch.words.SimpleGenerateGrid
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelHintTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private class FakeRepo(
        private val pack: WordPack
    ) : PackRepository {
        override suspend fun listBuiltinPacks(): List<String> = listOf(pack.packId)
        override suspend fun loadPack(packId: String): WordPack = pack
    }

    @Test
    fun hint_marks_exactly_one_unfound_cell() = runTest {
        val words = listOf("Wasser", "Apfel", "Brot")
        val pack = WordPack(
            packId = "test",
            sourceLang = "de",
            targetLang = "en",
            title = "Test Pack",
            words = words.map { WordEntry(it, "x") }
        )
        val vm = GameViewModel(
            repo = FakeRepo(pack),
            gridGen = SimpleGenerateGrid(Random(1234)),
            computationDispatcher = mainRule.dispatcher
        )

        advanceUntilIdle()

        val beforeHintCount = vm.uiState.value.grid.flatten().count { it.isHint }
        vm.giveHint()
        val s = vm.uiState.value
        val afterHintCount = s.grid.flatten().count { it.isHint }

        assertEquals(beforeHintCount + 1, afterHintCount)
        assertEquals(1, s.hintsUsed)
        // none of the hinted cells should be found
        assertTrue(s.grid.flatten().filter { it.isHint }.none { it.isFound })
    }
}
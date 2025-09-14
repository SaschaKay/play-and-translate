package com.playandtranslate.wordsearch.ui

import com.playandtranslate.wordsearch.data.PackRepository
import com.playandtranslate.wordsearch.data.WordEntry
import com.playandtranslate.wordsearch.data.WordPack
import com.playandtranslate.wordsearch.testutil.MainDispatcherRule
import com.playandtranslate.wordsearch.words.SimpleGenerateGrid
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeRepo(
        private val pack: WordPack
    ) : PackRepository {
        override suspend fun listBuiltinPacks(): List<String> = listOf(pack.packId)
        override suspend fun loadPack(packId: String): WordPack = pack
    }

    @Test
    fun loads_grid_and_placements() = runTest {
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
            computationDispatcher = mainDispatcherRule.dispatcher
        )

        advanceUntilIdle()
        val s = vm.uiState.value

        assertFalse(s.isLoading)
        assertNull(s.error)
        assertTrue(s.grid.isNotEmpty())
        assertTrue(s.placements.isNotEmpty())

        // every placement is within grid bounds
        s.placements.forEach { wp ->
            wp.cells.forEach { p ->
                assertTrue(p.row in s.grid.indices)
                assertTrue(p.col in s.grid.first().indices)
            }
        }
    }

    @Test
    fun selecting_start_and_end_marks_word_found() = runTest {
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
            computationDispatcher = mainDispatcherRule.dispatcher
        )

        advanceUntilIdle()
        val s1 = vm.uiState.value
        val placement = s1.placements.first()

        val start = placement.cells.first()
        val end = placement.cells.last()

        vm.onCellTap(start.row, start.col)
        vm.onCellTap(end.row, end.col)

        val s2 = vm.uiState.value
        assertTrue(0 in s2.found)
        assertTrue(placement.cells.all { p -> s2.grid[p.row][p.col].isFound })
    }

    @Test
    fun reversed_taps_also_match() = runTest {
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
            gridGen = SimpleGenerateGrid(Random(9876)),
            computationDispatcher = mainDispatcherRule.dispatcher
        )

        advanceUntilIdle()
        val s1 = vm.uiState.value
        val placement = s1.placements.first()

        val start = placement.cells.last()
        val end = placement.cells.first()

        vm.onCellTap(start.row, start.col)
        vm.onCellTap(end.row, end.col)

        val s2 = vm.uiState.value
        assertTrue(0 in s2.found)
    }
}
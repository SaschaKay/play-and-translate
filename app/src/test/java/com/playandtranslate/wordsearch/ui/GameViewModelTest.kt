package com.playandtranslate.wordsearch.ui

import com.playandtranslate.wordsearch.data.PackRepository
import com.playandtranslate.wordsearch.data.WordEntry
import com.playandtranslate.wordsearch.data.WordPack
import com.playandtranslate.wordsearch.domain.*
import com.playandtranslate.wordsearch.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    // --- Fakes ---
    private class FakeRepo : PackRepository {
        override suspend fun listBuiltinPacks(): List<String> = listOf("fake")
        override suspend fun loadPack(packId: String): WordPack =
            WordPack(
                packId = "fake",
                version = 1,
                sourceLang = "de",
                targetLang = "en",
                direction = "LTR",
                title = "Fake Pack",
                origin = "builtin",
                words = listOf(
                    WordEntry("Hund", "dog"),
                    WordEntry("Katze", "cat")
                )
            )
    }

    private class FakeGridGen : GenerateGrid {
        override fun createGrid(
            words: List<String>,
            size: Int,
            diagonals: Boolean,
            allowIntersections: Boolean
        ): GridBuild {
            // Deterministic 2x2 grid with placements for easy assertions
            val chars = listOf(
                listOf('H', 'U'),
                listOf('N', 'D')
            )
            val placements = listOf(
                WordPlacement(
                    original = "Hund",
                    normalized = "HUND",
                    cells = listOf(Pos(0,0), Pos(0,1), Pos(1,1), Pos(1,0)) // arbitrary path
                )
            )
            return GridBuild(grid = chars, placements = placements, skipped = emptyList())
        }
    }

    @Test
    fun `loads pack and exposes grid+placements`() = runTest {
        val vm = GameViewModel(
            repo = FakeRepo(),
            gridGen = FakeGridGen()
        )

        // allow viewModelScope coroutines to run
        advanceUntilIdle()

        val state = vm.uiState.value

        // Title propagated
        assertEquals("Fake Pack", state.packTitle)

        // Grid mapped to Cell objects, same dimensions as chars (2x2)
        assertEquals(2, state.grid.size)
        assertEquals(2, state.grid[0].size)
        assertEquals('H', state.grid[0][0].letter)
        assertEquals('U', state.grid[0][1].letter)
        assertEquals('N', state.grid[1][0].letter)
        assertEquals('D', state.grid[1][1].letter)

        // Placements surfaced
        assertTrue(state.placements.any { it.normalized == "HUND" })
        assertTrue(state.skipped.isEmpty())

        // Not loading, no error
        assertEquals(false, state.isLoading)
        assertEquals(null, state.error)
    }
}
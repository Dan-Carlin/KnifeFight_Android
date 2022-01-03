package com.flounderguy.knifefightutilities.ui.setup.finalstep

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SetupFinalStepViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()
    private lateinit var finalStepViewModel: SetupFinalStepViewModel

    private val testGang = Gang(
        name = "Test Gang",
        color = Gang.Color.BLACK,
        trait = Gang.Trait.FIERCE,
        isUser = true,
        isDefeated = false,
        id = 0
    )

    private val testGangFlow = flowOf(testGang)
    private val testTraitList = mockk<Flow<List<CharacterTrait>>>()

    private val repository = mockk<KnifeFightRepository> {
        coEvery { getUserGang() } returns testGangFlow
        coEvery { getTraitList() } returns testTraitList
        coEvery { insertGang(any()) } just runs
    }

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        finalStepViewModel = SetupFinalStepViewModel(repository)
    }

    @Test
    fun `trait value from flow successfully stored when onFinalStepStarted()`() = runBlockingTest {
        // When
        finalStepViewModel.onFinalStepStarted()

        // Then
        assertThat(finalStepViewModel.userTrait).isEqualTo(testGang.trait)
    }

    @Test
    fun `CharacterTrait is successfully added to list when onTraitSelected()`() {
        // Given
        val testRivalGangTraits = listOf(Gang.Trait.FIERCE)
        val testCharacterTrait = mockk<CharacterTrait>()

        // When
        coEvery { testCharacterTrait.name } returns "Fierce"
        finalStepViewModel.onTraitSelected(testCharacterTrait)

        // Then
        assertThat(finalStepViewModel.rivalGangTraits).isEqualTo(testRivalGangTraits)
    }

    @Test
    fun `rivalsAreSelected set to true when trait is added to rivalGangTraits list`() {
        // Given
        val testCharacterTrait = mockk<CharacterTrait>()

        // When
        coEvery { testCharacterTrait.name } returns "Fierce"
        finalStepViewModel.onTraitSelected(testCharacterTrait)

        // Then
        finalStepViewModel.rivalsAreSelected.value?.let { assertTrue(it) }
    }

    @Test
    fun `isUserTrait returns true when input matches userTrait value`() {
        // Given
        val testCharacterTrait = mockk<CharacterTrait>()
        val testTrait = Gang.Trait.HEAVY

        // When
        coEvery { testCharacterTrait.name } returns "Heavy"
        finalStepViewModel.userTrait = testTrait

        // Then
        assertTrue(finalStepViewModel.isUserTrait(testCharacterTrait))
    }

    @Test
    fun `rival Gang objects are created when onSetupCompleted()`() {
        // Given
        val testGangList = listOf(
            Gang("", Gang.Color.NONE, Gang.Trait.BURLY, isUser = false, isDefeated = false)
        )
        val testCharacterTrait = mockk<CharacterTrait>()

        // When
        coEvery { testCharacterTrait.name } returns "Burly"
        finalStepViewModel.onTraitSelected(testCharacterTrait)
        finalStepViewModel.onSetupCompleted()

        // Then
        assertThat(finalStepViewModel.rivalGangList).isEqualTo(testGangList)
    }
}
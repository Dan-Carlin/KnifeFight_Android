package com.flounderguy.knifefightutilities.ui.setup.thirdstep

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
class SetupThirdStepViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()
    private lateinit var thirdStepViewModel: SetupThirdStepViewModel

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
        coEvery { userGangExists() } returns true
        coEvery { updateGang(any()) } just runs
    }

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        thirdStepViewModel = SetupThirdStepViewModel(repository)
    }

    @Test
    fun `values from flow successfully stored when onThirdStepStarted()`() = runBlockingTest {
        // When
        thirdStepViewModel.onThirdStepStarted()

        // Then
        assertThat(thirdStepViewModel.gangColor).isEqualTo(testGang.color)
        // and
        assertThat(thirdStepViewModel.gangTrait).isEqualTo(testGang.trait)
    }

    @Test
    fun `CharacterTrait is successfully converted to Trait enum when setUserTrait()`() = runBlockingTest {
        // Given
        val testTrait = Gang.Trait.LUCKY
        val testCharacterTrait = mockk<CharacterTrait>()

        // When
        coEvery { testCharacterTrait.name } returns "Lucky"
        // and
        thirdStepViewModel.setUserTrait(testCharacterTrait)

        // Then
        assertThat(thirdStepViewModel.gangTrait).isEqualTo(testTrait)
    }

    @Test
    fun `traitIsSelected set to true when gangTrait value is set`() {
        // Given
        val testTrait = Gang.Trait.BRASH

        // When
        thirdStepViewModel.gangTrait = testTrait

        // Then
        thirdStepViewModel.traitIsSelected.value?.let { assertTrue(it) }
    }

    @Test
    fun `onThirdStepCompleted() updates Gang with correct trait value`() = runBlockingTest {
        // Given
        val testTrait = Gang.Trait.EAGER
        val updatedGang = testGang.copy(trait = testTrait)

        // When
        thirdStepViewModel.onThirdStepStarted()
        // and
        thirdStepViewModel.gangTrait = testTrait
        // and
        thirdStepViewModel.onThirdStepCompleted()

        // Then
        coVerify { repository.updateGang(updatedGang) }
    }
}
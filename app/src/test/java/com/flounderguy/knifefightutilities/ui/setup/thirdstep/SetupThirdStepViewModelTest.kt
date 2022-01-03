package com.flounderguy.knifefightutilities.ui.setup.thirdstep

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    private val testTrait = Gang.Trait.LUCKY

    private val repository = mockk<KnifeFightRepository> {
        coEvery { getUserGang() } returns testGangFlow
        coEvery { getTraitList() } returns testTraitList
        coEvery { userGangExists() } returns true
        coEvery { updateGang(any()) } just runs
    }
    private val state = mockk<SavedStateHandle> {
        every { get<Gang.Trait>(any()) } returns testTrait
        every { set<Gang.Trait>(any(), any()) } just runs
    }

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        thirdStepViewModel = SetupThirdStepViewModel(repository, state)
    }

    @Test
    fun `values from flow successfully stored when onThirdStepStarted()`() = runBlockingTest {
        // When
        thirdStepViewModel.onThirdStepStarted()

        // Then
        assertThat(thirdStepViewModel.gangColor).isEqualTo(testGang.color)
        assertThat(thirdStepViewModel.gangTrait).isEqualTo(testGang.trait)
    }

    @Test
    fun `CharacterTrait is successfully converted to Trait enum when setUserTrait()`() = runBlockingTest {
        // Given
        val testCharacterTrait = mockk<CharacterTrait>()

        // When
        coEvery { testCharacterTrait.name } returns "Lucky"
        thirdStepViewModel.setUserTrait(testCharacterTrait)

        // Then
        assertThat(thirdStepViewModel.gangTrait).isEqualTo(testTrait)
    }

    @Test
    fun `traitIsSelected set to true when gangTrait value is set`() {
        // When
        thirdStepViewModel.gangTrait = testTrait

        // Then
        thirdStepViewModel.traitIsSelected.value?.let { assertTrue(it) }
    }

    @Test
    fun `onPreviousStepButtonClicked() updates Gang with new trait value and navigates back`() = runBlockingTest {
        // Given
        val updatedGang = testGang.copy(trait = testTrait)

        // When
        thirdStepViewModel.onThirdStepStarted()
        thirdStepViewModel.gangTrait = testTrait
        thirdStepViewModel.onPreviousStepButtonClicked()

        // Then
        coVerify { repository.updateGang(updatedGang) }
        assertThat(SetupThirdStepViewModel.ThirdStepEvent.NavigateBackToSecondStep)
            .isEqualTo(thirdStepViewModel.thirdStepEvent.first())
    }

    @Test
    fun `onThirdStepCompleted() updates Gang with new trait value and navigates forward`() = runBlockingTest {
        // Given
        val updatedGang = testGang.copy(trait = testTrait)

        // When
        thirdStepViewModel.onThirdStepStarted()
        thirdStepViewModel.gangTrait = testTrait
        thirdStepViewModel.onThirdStepCompleted()

        // Then
        coVerify { repository.updateGang(updatedGang) }
        assertThat(SetupThirdStepViewModel.ThirdStepEvent.NavigateToFinalStepScreen)
            .isEqualTo(thirdStepViewModel.thirdStepEvent.first())
    }
}
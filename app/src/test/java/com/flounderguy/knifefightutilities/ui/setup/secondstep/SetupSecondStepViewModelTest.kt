package com.flounderguy.knifefightutilities.ui.setup.secondstep

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import com.google.common.truth.Truth.assertThat
import junit.framework.Assert.assertTrue
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
class SetupSecondStepViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()
    private lateinit var secondStepViewModel: SetupSecondStepViewModel

    private val testGang = Gang(
        name = "Test Gang",
        color = Gang.Color.BLACK,
        trait = Gang.Trait.FIERCE,
        isUser = true,
        isDefeated = false,
        id = 0
    )
    private val testGangFlow = flowOf(testGang)
    private val testColor = Gang.Color.RED

    private val repository = mockk<KnifeFightRepository> {
        coEvery { getUserGang() } returns testGangFlow
        coEvery { userGangExists() } returns true
        coEvery { updateGang(any()) } just runs
    }
    private val state = mockk<SavedStateHandle> {
        every { get<Gang.Color>(any()) } returns testColor
        every { set<Gang.Color>(any(), any()) } just runs
    }

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        secondStepViewModel = SetupSecondStepViewModel(repository, state)
    }

    @Test
    fun `values from flow successfully stored when onSecondStepStarted()`() = runBlockingTest {
        // When
        secondStepViewModel.onSecondStepStarted()

        // Then
        assertThat(secondStepViewModel.gangColor).isEqualTo(testGang.color)
        assertThat(secondStepViewModel.gangTrait).isEqualTo(testGang.trait)
    }

    @Test
    fun `colorIsSelected set to true when gangColor value is set`() {
        // Given
        val testColor = Gang.Color.BLUE

        // When
        secondStepViewModel.gangColor = testColor

        // Then
        secondStepViewModel.colorIsSelected.value?.let { assertTrue(it) }
    }

    @Test
    fun `onPreviousStepButtonClicked() updates Gang with new color value and navigates back`() =
        runBlockingTest {
            // Given
            val updatedGang = testGang.copy(color = testColor)

            // When
            secondStepViewModel.onSecondStepStarted()
            secondStepViewModel.gangColor = testColor
            secondStepViewModel.onPreviousStepButtonClicked()

            // Then
            coVerify { repository.updateGang(updatedGang) }
            assertThat(SetupSecondStepViewModel.SecondStepEvent.NavigateBackToFirstStep)
                .isEqualTo(secondStepViewModel.secondStepEvent.first())
        }

    @Test
    fun `onSecondStepCompleted() updates Gang with new color value and navigates forward`() =
        runBlockingTest {
            // Given
            val updatedGang = testGang.copy(color = testColor)

            // When
            secondStepViewModel.onSecondStepStarted()
            secondStepViewModel.gangColor = testColor
            secondStepViewModel.onSecondStepCompleted()

            // Then
            coVerify { repository.updateGang(updatedGang) }
            assertThat(
                SetupSecondStepViewModel.SecondStepEvent.NavigateToThirdStepScreen(
                    secondStepViewModel.gangTrait
                )
            ).isEqualTo(secondStepViewModel.secondStepEvent.first())
        }
}
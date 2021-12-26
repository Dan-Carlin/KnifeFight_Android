package com.flounderguy.knifefightutilities.ui.setup.secondstep

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import com.google.common.truth.Truth.assertThat
import junit.framework.Assert.assertTrue
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

    private val repository = mockk<KnifeFightRepository> {
        coEvery { getUserGang() } returns testGangFlow
        coEvery { userGangExists() } returns true
        coEvery { updateGang(any()) } just runs
    }

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        secondStepViewModel = SetupSecondStepViewModel(repository)
    }

    @Test
    fun `values from flow successfully stored when onSecondStepStarted()`() = runBlockingTest {
        // When
        secondStepViewModel.onSecondStepStarted()

        // Then
        assertThat(secondStepViewModel.gangColor).isEqualTo(testGang.color)
        // and
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
    fun `onSecondStepCompleted() updates Gang with correct color value`() = runBlockingTest {
        // Given
        val testColor = Gang.Color.RED
        val updatedGang = testGang.copy(color = testColor)

        // When
        secondStepViewModel.onSecondStepStarted()
        // and
        secondStepViewModel.gangColor = testColor
        // and
        secondStepViewModel.onSecondStepCompleted()

        // Then
        coVerify { repository.updateGang(updatedGang) }
    }
}
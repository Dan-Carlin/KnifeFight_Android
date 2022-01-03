package com.flounderguy.knifefightutilities.ui.setup.firststep

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
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
class SetupFirstStepViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()
    private lateinit var firstStepViewModel: SetupFirstStepViewModel

    private val testGang = Gang(
        name = "Test Gang",
        color = Gang.Color.BLACK,
        trait = Gang.Trait.FIERCE,
        isUser = true,
        isDefeated = false,
        id = 0
    )

    private val testGangFlow = flowOf(testGang)
    private val nullGangFlow = flowOf<Gang>()

    private val repository = mockk<KnifeFightRepository> {
        coEvery { getUserGang() } returns testGangFlow
        coEvery { userGangExists() } returns true
        coEvery { updateGang(any()) } just runs
        coEvery { insertGang(any()) } just runs
    }
    private val state = mockk<SavedStateHandle>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        firstStepViewModel = SetupFirstStepViewModel(repository, state)
    }

    @Test
    fun `values from flow successfully stored when onFirstStepStarted()`() = runBlockingTest {
        // When
        firstStepViewModel.onFirstStepStarted()

        // Then
        assertThat(firstStepViewModel.gangName).isEqualTo(testGang.name)
        // and
        assertThat(firstStepViewModel.gangColor).isEqualTo(testGang.color)
    }

    @Test
    fun `randomized string is generated when onGenerateNameButtonPressed()`() {
        // When
        val randomName = firstStepViewModel.onGenerateNameButtonPressed()

        // Then
        assertThat(randomName).isNotNull()
    }

    @Test
    fun `onFirstStepCompleted() updates Gang when userGang is not null and navigates forward`() =
        runBlockingTest {
            // When
            firstStepViewModel.onFirstStepStarted()
            // and
            firstStepViewModel.onFirstStepCompleted()

            // Then
            coVerify { firstStepViewModel.userGang.value?.let { repository.updateGang(it) } }
            // and
            assertThat(
                SetupFirstStepViewModel.FirstStepEvent.NavigateToSecondStepScreen(
                    firstStepViewModel.gangColor
                )
            ).isEqualTo(firstStepViewModel.firstStepEvent.first())
        }

    @Test
    fun `onFirstStepCompleted() inserts new Gang when userGang is null and navigates forward`() =
        runBlockingTest {
            nullGangFlow.collect {
                // Given
                every { firstStepViewModel.userGang.value } returns it

                // When
                firstStepViewModel.onFirstStepStarted()
                // and
                firstStepViewModel.onFirstStepCompleted()

                // Then
                coVerify { repository.insertGang(any()) }
                // and
                assertThat(
                    SetupFirstStepViewModel.FirstStepEvent.NavigateToSecondStepScreen(
                        firstStepViewModel.gangColor
                    )
                ).isEqualTo(firstStepViewModel.firstStepEvent.first())
            }
        }
}
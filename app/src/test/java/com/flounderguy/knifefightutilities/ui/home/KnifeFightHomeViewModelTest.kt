package com.flounderguy.knifefightutilities.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class KnifeFightHomeViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()
    private lateinit var homeViewModel: KnifeFightHomeViewModel

    private val repository = mockk<KnifeFightRepository> {
        coEvery { clearGangs() } just runs
        coEvery { rivalGangsExist() } returns true
    }
    private val state = mockk<SavedStateHandle>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        homeViewModel = KnifeFightHomeViewModel(repository, state)
    }

    @Test
    fun `onNewGameStarted verifies existing game when activeGame is true`() = runBlockingTest {
        // When
        homeViewModel.onAppStarted()
        homeViewModel.onNewGameStarted()

        // Then
        assertThat(KnifeFightHomeViewModel.HomeEvent.NavigateToConfirmNewGameScreen)
            .isEqualTo(homeViewModel.homeEvent.first())
    }

    @Test
    fun `onNewGameStarted navigates to first step when activeGame is false`() = runBlockingTest {
        // When
        coEvery { repository.rivalGangsExist() } returns false
        homeViewModel.onAppStarted()
        homeViewModel.onNewGameStarted()

        // Then
        assertThat(KnifeFightHomeViewModel.HomeEvent.NavigateToFirstStepScreen)
            .isEqualTo(homeViewModel.homeEvent.first())
    }
}
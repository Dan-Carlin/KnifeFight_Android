package com.flounderguy.knifefightutilities.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
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
    private lateinit var repository: KnifeFightRepository
    private lateinit var state: SavedStateHandle

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repository = mockk {
            coEvery { clearGangs() } just runs
            coEvery { userGangExists() } returns true
        }
        state = mockk(relaxed = true)

        homeViewModel = KnifeFightHomeViewModel(repository, state)
    }

    @Test
    fun `onNewGameStarted verifies existing game when activeGame is true`() = runBlockingTest {
        // Given

        // When
        homeViewModel.onNewGameStarted()

        // Then
        assertEquals(
            KnifeFightHomeViewModel.HomeEvent.NavigateToConfirmNewGameScreen,
            homeViewModel.homeEvent.first()
        )
    }

    @Test
    fun `onNewGameStarted navigates to first step when activeGame is false`() = runBlockingTest {
        // Given

        // When
        homeViewModel.onNewGameStarted()

        // Then
        assertNotEquals(
            KnifeFightHomeViewModel.HomeEvent.NavigateToFirstStepScreen,
            homeViewModel.homeEvent.first()
        )
    }
}
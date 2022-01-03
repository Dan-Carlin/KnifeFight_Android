package com.flounderguy.knifefightutilities.ui.home.confirmnewgame

import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class HomeConfirmNewGameViewModelTest {

    private val dispatcher = TestCoroutineDispatcher()

    private lateinit var confirmViewModel: HomeConfirmNewGameViewModel
    private lateinit var repository: KnifeFightRepository

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repository = mockk { coEvery { clearGangs() } just runs }

        confirmViewModel = HomeConfirmNewGameViewModel(repository)
    }

    @Test
    fun `database cleared when onConfirmClick`() {
        // When
        confirmViewModel.onConfirmClick()

        // Then
        coVerify { repository.clearGangs() }
    }
}
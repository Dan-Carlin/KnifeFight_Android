package com.flounderguy.knifefightutilities.ui.setup.secondstep

import androidx.lifecycle.SavedStateHandle
import com.flounderguy.knifefightutilities.data.Gang
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SetupSecondStepViewModelTest {

    private val dispatcher = TestCoroutineDispatcher()

    private lateinit var secondStepViewModel: SetupSecondStepViewModel
    private lateinit var state: SavedStateHandle

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        state = mockk<SavedStateHandle>(relaxed = true)

        secondStepViewModel = SetupSecondStepViewModel(state)
    }

    @Test
    fun `user input saves Color in SavedStateHandle`() {
        // Given
        val gangColor = Gang.GangColor.BLACK

        // When
        secondStepViewModel.gangColor = gangColor

        // Then
        assertEquals(gangColor, secondStepViewModel.gangColor)
        verify{ state.set("color", gangColor) }
    }
}
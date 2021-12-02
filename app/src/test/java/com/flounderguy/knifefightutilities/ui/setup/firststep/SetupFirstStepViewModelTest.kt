package com.flounderguy.knifefightutilities.ui.setup.firststep

import androidx.lifecycle.SavedStateHandle
import io.mockk.*
import junit.framework.Assert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SetupFirstStepViewModelTest {

    private val dispatcher = TestCoroutineDispatcher()
    private lateinit var firstStepViewModel: SetupFirstStepViewModel
    private lateinit var state: SavedStateHandle

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        state = mockk(relaxed = true)

        firstStepViewModel = SetupFirstStepViewModel(state)
    }

    @Test
    fun `user input setter saves name in SavedStateHandle`() {
        // Given
        val gangName = "The Homies"

        // When
        firstStepViewModel.gangName = gangName

        // Then
        assertEquals(gangName, firstStepViewModel.gangName)
        verify{ state.set("name", gangName) }
    }

    @Test
    fun `randomized string is generated when generateGangName called`() {
        // Given
        val randomName: String

        // When
        randomName = firstStepViewModel.generateGangName()

        // Then
        assertNotNull(randomName)
    }
}
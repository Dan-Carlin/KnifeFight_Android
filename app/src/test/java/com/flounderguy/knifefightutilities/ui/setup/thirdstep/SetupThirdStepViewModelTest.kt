package com.flounderguy.knifefightutilities.ui.setup.thirdstep

import androidx.lifecycle.SavedStateHandle
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SetupThirdStepViewModelTest {

    private val dispatcher = TestCoroutineDispatcher()
    private val gangName = "The Homies"
    private val gangColor = Gang.GangColor.BLACK
    private val gangTrait = Gang.Trait.ADVENTUROUS
    private val newGang = Gang(gangName, gangColor, gangTrait, isUser = true, isDefeated = false)

    private lateinit var thirdStepViewModel: SetupThirdStepViewModel
    private lateinit var repository: KnifeFightRepository
    private lateinit var state: SavedStateHandle

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repository = mockk{ coEvery { insertGang(any()) } just runs }
        state = mockk(relaxed = true) {
            every { get<String>("name") } returns gangName
            every { get<Gang.GangColor>("color") } returns gangColor
        }

        thirdStepViewModel = SetupThirdStepViewModel(repository, state)
    }

    @Test
    fun `create gang and navigate when onSetupCompleted`() = runBlockingTest {
        // Given
        thirdStepViewModel.gangTrait = gangTrait

        // When
        thirdStepViewModel.onThirdStepCompleted()

        // Then
        coVerify { repository.insertGang(newGang) }
        assertEquals(
            SetupThirdStepViewModel.ThirdStepEvent.NavigateToFinalStepScreen,
            thirdStepViewModel.thirdStepEvent.first()
        )
    }
}
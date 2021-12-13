package com.flounderguy.knifefightutilities.ui.setup.thirdstep

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.flounderguy.knifefightutilities.TestCoroutineRule
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
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
    private val testScope = TestCoroutineScope(dispatcher)

    private val gangName = "The Homies"
    private val gangColor = Gang.Color.BLACK
    private val gangTrait = Gang.Trait.ADVENTUROUS
    private val newGang = Gang(gangName, gangColor, gangTrait, isUser = true, isDefeated = false)
    private val traitList = mockk<Flow<List<CharacterTrait>>>()

    private lateinit var thirdStepViewModel: SetupThirdStepViewModel
    private lateinit var repository: KnifeFightRepository
    private lateinit var state: SavedStateHandle

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repository = mockk {
            coEvery { updateGang(any()) } just runs
            coEvery { getTraitList() } returns traitList
            coEvery { insertGang(any()) } just runs
        }
        state = mockk(relaxed = true) {
            every { get<Gang>("gang") } returns newGang
            every { get<String>("name") } returns gangName
            every { get<Gang.Color>("color") } returns gangColor
        }

        testScope.launch { thirdStepViewModel = SetupThirdStepViewModel(repository, state) }
    }

    @Test
    fun `update gang and navigate when onSetupCompleted`() = testScope.runBlockingTest {
        // Given
        thirdStepViewModel.gangTrait = gangTrait

        // When
        testScope.launch { thirdStepViewModel.onThirdStepCompleted() }

        // Then
        coVerify { repository.updateGang(newGang) }
        assertEquals(
            SetupThirdStepViewModel.ThirdStepEvent.NavigateToFinalStepScreen(newGang),
            thirdStepViewModel.thirdStepEvent.first()
        )
    }
}
package com.flounderguy.knifefightutilities

import androidx.lifecycle.SavedStateHandle
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.GangDao
import com.flounderguy.knifefightutilities.ui.setup.firststep.SetupFirstStepViewModel
import com.flounderguy.knifefightutilities.ui.setup.secondstep.SetupSecondStepViewModel
import com.flounderguy.knifefightutilities.ui.setup.thirdstep.SetupThirdStepViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SetupThirdStepViewModelTest {

    private val dispatcher = TestCoroutineDispatcher()

    private lateinit var firstStepViewModel: SetupFirstStepViewModel
    private lateinit var secondStepViewModel: SetupSecondStepViewModel
    private lateinit var thirdStepViewModel: SetupThirdStepViewModel

    private val gangDao = mockk<GangDao>{ coEvery { insert(any()) } just runs}
    private val state = mockk<SavedStateHandle>(relaxed = true)

    private val gangName = "Ganggang"
    private val gangColor = Gang.GangColor.BLACK
    private val gangTrait = Gang.Trait.ADVENTUROUS
    private val newGang = Gang(gangName, gangColor, gangTrait)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        firstStepViewModel = SetupFirstStepViewModel(state)
        secondStepViewModel = SetupSecondStepViewModel(state)
        thirdStepViewModel = SetupThirdStepViewModel(gangDao, state)

        firstStepViewModel.gangName = gangName
        secondStepViewModel.gangColor = gangColor
        thirdStepViewModel.gangTrait = gangTrait
        thirdStepViewModel.thirdStepEvent
    }

    @Test
    fun `create gang and navigate when onSetupCompleted`() = runBlockingTest {
        //given

        //when
        thirdStepViewModel.onThirdStepCompleted()

        //then
        coVerify { gangDao.insert(newGang) }
        Assert.assertEquals(
            SetupThirdStepViewModel.ThirdStepEvent.NavigateToFinalStepScreen(newGang),
            thirdStepViewModel.thirdStepEvent.first()
        )
    }

}
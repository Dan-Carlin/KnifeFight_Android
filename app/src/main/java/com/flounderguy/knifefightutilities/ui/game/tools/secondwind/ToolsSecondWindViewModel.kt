package com.flounderguy.knifefightutilities.ui.game.tools.secondwind

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import com.flounderguy.knifefightutilities.util.DieType
import com.flounderguy.knifefightutilities.util.ResultRequest
import com.flounderguy.knifefightutilities.util.rollDice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the ToolsSecondWindFragment.
 * This ViewModel is in charge of:
 *      - Enabling the user to roll for a chance at a Second Wind revival.
 *      - Verifying the success of the roll.
 *      - Determining whether or not the game is over.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class ToolsSecondWindViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    init {
        checkSecondWindSuccess()
        onResultObtained()
    }

    /**
     * Variables for Second Wind output and result
     */
    // This holds the value of the d20 roll that will determine game over status.
    private val _secondWindOutput = MutableLiveData<Int>()
    val secondWindOutput: LiveData<Int>
        get() = _secondWindOutput

    private val _secondWindResult = MutableLiveData<Int>(state.get("second_wind_result"))
    val secondWindResult: LiveData<Int>
        get() = _secondWindResult

    private val _secondWindIsSuccessful = MutableLiveData<Boolean>()
    val secondWindIsSuccessful: LiveData<Boolean>
        get() = _secondWindIsSuccessful

    /**
     * Event channel for SecondWindEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val secondWindEventChannel = Channel<SecondWindEvent>()
    val secondWindEvent = secondWindEventChannel.receiveAsFlow()

    /**
     * Event functions for the SecondWind fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    private fun onSecondWindRoll(output: Int) = viewModelScope.launch {
        secondWindEventChannel.send(
            SecondWindEvent.ShowResultsMessage(output, 0, ResultRequest.SECOND_WIND)
        )
    }

    private fun onResultObtained() = viewModelScope.launch {
        if (secondWindIsSuccessful.value == true) {
            secondWindEventChannel.send(SecondWindEvent.NavigateBackToGameToolsMenuScreen)
        } else {
            secondWindEventChannel.send(SecondWindEvent.NavigateToGameOverScreen)
        }
    }

    /**
     * Gameplay functions
     */
    fun rollForSecondWind() {
        _secondWindOutput.value = rollDice(DieType.D20)
        checkCounterForNullAndNavigate()
    }

    /**
     * Private functions
     */
    private fun checkCounterForNullAndNavigate() {
        if (secondWindOutput.value != null) {
            onSecondWindRoll(secondWindOutput.value!!)
        }
    }

    private fun checkSecondWindSuccess() {
        _secondWindIsSuccessful.value = _secondWindResult.value == 20
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class SecondWindEvent {
        data class ShowResultsMessage(
            val output: Int,
            val modifier: Int,
            val request: ResultRequest
        ) : SecondWindEvent()

        object NavigateToGameOverScreen : SecondWindEvent()
        object NavigateBackToGameToolsMenuScreen : SecondWindEvent()
    }
}
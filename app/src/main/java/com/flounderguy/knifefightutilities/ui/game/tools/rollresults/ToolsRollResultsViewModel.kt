package com.flounderguy.knifefightutilities.ui.game.tools.rollresults

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.util.ResultRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the ToolsRollResultsFragment.
 * This ViewModel is in charge of:
 *      - Receiving a pair of values to work with as well as a ResultRequest type.
 *      - Adding up the two values and storing the result in a variable.
 *      - Making all values available to its own fragment for display.
 *      - Packing up the result in the SavedStateHandle with a key unique to the requesting fragment.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class ToolsRollResultsViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Variables provided by requesting fragment
     */
    // This creates variables that store the data sent by the fragment needing a result.
    private val request = state.get<ResultRequest>("request")

    private val _rollOutput = MutableLiveData<Int>(state.get("output"))
    val rollOutput: LiveData<Int>
        get() = _rollOutput

    private val _statModifier = MutableLiveData<Int>(state.get("modifier"))
    val statModifier: LiveData<Int>
        get() = _statModifier

    // This calculates the total to send back.
    private val _resultTotal = MutableLiveData<Int>(rollOutput.value?.plus(statModifier.value!!))
    val resultTotal: LiveData<Int>
        get() = _resultTotal

    init {
        when (request) {
            ResultRequest.ATTACK -> state.set("attack_result", _resultTotal.value)
            ResultRequest.DAMAGE -> state.set("damage_result", _resultTotal.value)
            ResultRequest.COUNTER -> state.set("counter_result", _resultTotal.value)
            ResultRequest.TIE -> state.set("tie_result", _resultTotal.value)
            ResultRequest.SECOND_WIND -> state.set("second_wind_result", _resultTotal.value)
        }
    }

    /**
     * Event channel for RollResultsEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val rollResultsEventChannel = Channel<RollResultsEvent>()
    val rollResultsEvent = rollResultsEventChannel.receiveAsFlow()

    /**
     * Event functions for the RollResults fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onConfirmClicked() = viewModelScope.launch {
        rollResultsEventChannel.send(RollResultsEvent.NavigateBackToPreviousScreen)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class RollResultsEvent {
        object NavigateBackToPreviousScreen : RollResultsEvent()
    }

}
package com.flounderguy.knifefightutilities.ui.setup.secondstep

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flounderguy.knifefightutilities.data.Gang
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the SetupSecondStepFragment.
 * This ViewModel is in charge of:
 *      - Taking input from the user for a gang color selection.
 *      - Saving this value for the Gang object creation in the third step.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class SetupSecondStepViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Color value for the user Gang object
     */
    // This takes the color selected by the user and saves it for the Gang object creation in the
    // third step.
    var gangColor = Gang.GangColor.NONE
        set(value) {
            field = value
            state.set("color", value)
        }

    /**
     * Event channel for SecondStepEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val secondStepEventChannel = Channel<SecondStepEvent>()
    val secondStepEvent = secondStepEventChannel.receiveAsFlow()

    /**
     * Event functions for the SecondStep fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onSecondStepCompleted() = viewModelScope.launch {
        secondStepEventChannel.send(SecondStepEvent.NavigateToThirdStepScreen(gangColor))
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class SecondStepEvent {
        data class NavigateToThirdStepScreen(val color: Gang.GangColor) :
            SecondStepEvent()
    }
}
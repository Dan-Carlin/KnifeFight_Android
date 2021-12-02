package com.flounderguy.knifefightutilities.ui.setup.thirdstep

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the SetupThirdStepFragment.
 * This ViewModel is in charge of:
 *      - Taking input from the user for the gang trait card that they drew.
 *      - Using all input gathered to create and store a Gang object for the user.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class SetupThirdStepViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * All values for the user Gang object
     */
    // This fetches the values from the last two steps and creates the final one necessary for the
    // user Gang.
    private val gangName = state.get<String>("name")
    private val gangColor = state.get<Gang.GangColor>("color")
    var gangTrait = Gang.Trait.NONE
        set(value) {
            field = value
            state.set("trait", value)
        }

    /**
     * Event channel for ThirdStepEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val thirdStepEventChannel = Channel<ThirdStepEvent>()
    val thirdStepEvent = thirdStepEventChannel.receiveAsFlow()

    /**
     * Event functions for the ThirdStep fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onThirdStepCompleted() = viewModelScope.launch {
        // This performs a null check on the variables from the last two steps and uses all the input
        // gathered to create a user Gang object to store in the database.
        if (gangName != null && gangColor != null) {
            val newGang = Gang(gangName, gangColor, gangTrait, isUser = true, isDefeated = false)
            createUserGang(newGang)
            thirdStepEventChannel.send(ThirdStepEvent.NavigateToFinalStepScreen)
        }
    }

    /**
     * Private functions
     */
    // This stores the user Gang in the database.
    private fun createUserGang(gang: Gang) = viewModelScope.launch {
        repository.insertGang(gang)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class ThirdStepEvent {
        object NavigateToFinalStepScreen : ThirdStepEvent()
    }
}
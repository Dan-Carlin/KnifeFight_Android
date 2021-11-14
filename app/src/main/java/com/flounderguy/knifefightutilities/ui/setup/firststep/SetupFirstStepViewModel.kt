package com.flounderguy.knifefightutilities.ui.setup.firststep

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupFirstStepViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Variable to hold name input in case of process death
     */
    val gangNameInput = state.getLiveData("gangNameInput", "")

    /**
     * Name value for the user Gang object
     */
    var gangName = ""
        set(value) {
            field = value
            state.set("name", value)
        }

    /**
     * Navigation event channels
     */
    private val firstStepEventChannel = Channel<FirstStepEvent>()
    val firstStepEvent = firstStepEventChannel.receiveAsFlow()

    /**
     * Navigation function for SetupFirstStepFragment
     */
    fun onFirstStepCompleted() = viewModelScope.launch {
        firstStepEventChannel.send(FirstStepEvent.NavigateToSecondStepScreen(gangName))
    }

    /**
     * Navigation event for SetupFirstStepFragment
     */
    sealed class FirstStepEvent {
        data class NavigateToSecondStepScreen(val name: String) : FirstStepEvent()
    }
}
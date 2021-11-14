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

@HiltViewModel
class SetupSecondStepViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Color value for the user Gang object
     */
    var gangColor = Gang.GangColor.NONE
        set(value) {
            field = value
            state.set("color", value)
        }

    /**
     * Navigation event channels
     */
    private val secondStepEventChannel = Channel<SecondStepEvent>()
    val secondStepEvent = secondStepEventChannel.receiveAsFlow()

    /**
     * Navigation function for SetupSecondStepFragment
     */
    fun onSecondStepCompleted() = viewModelScope.launch {
        secondStepEventChannel.send(SecondStepEvent.NavigateToThirdStepScreen(gangColor))
    }

    /**
     * Navigation event for SetupSecondStepFragment
     */
    sealed class SecondStepEvent {
        data class NavigateToThirdStepScreen(val color: Gang.GangColor) :
            SecondStepEvent()
    }
}
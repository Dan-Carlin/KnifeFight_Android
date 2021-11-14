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

@HiltViewModel
class SetupThirdStepViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * All values for the user Gang object
     */
    private val gangName = state.get<String>("name")
    private val gangColor = state.get<Gang.GangColor>("color")
    var gangTrait = Gang.Trait.NONE
        set(value) {
            field = value
            state.set("trait", value)
        }

    /**
     * Navigation event channel
     */
    private val thirdStepEventChannel = Channel<ThirdStepEvent>()
    val thirdStepEvent = thirdStepEventChannel.receiveAsFlow()

    /**
     * Navigation function for SetupThirdStepFragment
     */
    // Function takes input from all setup steps and submits a Gang object into the database
    fun onThirdStepCompleted() = viewModelScope.launch {
        if (gangName != null && gangColor != null) {
            val newGang = Gang(gangName, gangColor, gangTrait, isUser = true, isDefeated = false)
            createUserGang(newGang)
            thirdStepEventChannel.send(ThirdStepEvent.NavigateToFinalStepScreen)
        }
    }

    /**
     * Function to store Gang object in the database
     */
    private fun createUserGang(gang: Gang) = viewModelScope.launch {
        repository.insertGang(gang)
    }

    /**
     * Navigation event for SetupThirdStepFragment
     */
    sealed class ThirdStepEvent {
        object NavigateToFinalStepScreen : ThirdStepEvent()
    }
}
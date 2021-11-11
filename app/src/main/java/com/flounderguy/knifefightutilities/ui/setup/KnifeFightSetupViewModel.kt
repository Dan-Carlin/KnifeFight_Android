package com.flounderguy.knifefightutilities.ui.setup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.GangDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KnifeFightSetupViewModel @Inject constructor(
    private val gangDao: GangDao,
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Variable to hold name input in case of process death
     */
    val gangNameInput = state.getLiveData("gangNameInput", "")

    /**
     * Values required to create a new Gang object
     */
    var gangName = ""
        set(value) {
            field = value
            state.set("name", value)
        }

    var gangColor = Gang.GangColor.NONE
        set(value) {
            field = value
            state.set("color", value)
        }

    var traitLabel = Gang.Trait.NONE
        set(value) {
            field = value
            state.set("trait", value)
        }

    /**
     * Navigation event channels
     */
    private val homeEventChannel = Channel<HomeEvent>()
    val homeEvent = homeEventChannel.receiveAsFlow()

    private val firstStepEventChannel = Channel<FirstStepEvent>()
    val firstStepEvent = firstStepEventChannel.receiveAsFlow()

    private val secondStepEventChannel = Channel<SecondStepEvent>()
    val secondStepEvent = secondStepEventChannel.receiveAsFlow()

    private val thirdStepEventChannel = Channel<ThirdStepEvent>()
    val thirdStepEvent = thirdStepEventChannel.receiveAsFlow()

    /**
     * Navigation functions for the Home fragment
     */
    fun onInfoClicked() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToInfoScreen)
    }

    fun onSettingsClicked() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToSettingsScreen)
    }

    fun onNewGameStarted() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToFirstStepScreen)
    }

    /**
     * Navigation functions for the Setup fragments
     */
    fun onFirstStepCompleted() = viewModelScope.launch {
        firstStepEventChannel.send(FirstStepEvent.NavigateToSecondStepScreen)
    }

    fun onSecondStepCompleted() = viewModelScope.launch {
        secondStepEventChannel.send(SecondStepEvent.NavigateToThirdStepScreen)
    }

    // Function that actually creates the Gang object to be passed on to GameTools
    fun onSetupCompleted() = viewModelScope.launch {
        val newGang = Gang(gangName, gangColor, traitLabel)
        createGang(newGang)
        thirdStepEventChannel.send(ThirdStepEvent.NavigateToGameToolsScreen(newGang))
    }

    /**
     * Function to store Gang object in the database
     */
    private fun createGang(gang: Gang) = viewModelScope.launch {
        gangDao.insert(gang)
    }

    /**
     * Navigation events for Home and Setup fragments
     * */
    sealed class HomeEvent {
        object NavigateToFirstStepScreen : HomeEvent()
        object NavigateToInfoScreen : HomeEvent()
        object NavigateToSettingsScreen : HomeEvent()
    }

    sealed class FirstStepEvent {
        object NavigateToSecondStepScreen : FirstStepEvent()
    }

    sealed class SecondStepEvent {
        object NavigateToThirdStepScreen : SecondStepEvent()
    }

    sealed class ThirdStepEvent {
        data class NavigateToGameToolsScreen(val gang: Gang) : ThirdStepEvent()
    }
}
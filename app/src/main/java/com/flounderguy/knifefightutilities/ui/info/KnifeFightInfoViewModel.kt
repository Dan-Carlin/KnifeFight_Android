package com.flounderguy.knifefightutilities.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the KnifeFightInfoFragment.
 * This ViewModel is in charge of:
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class KnifeFightInfoViewModel @Inject constructor() : ViewModel() {

    /**
     * Event channel for InfoEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val infoEventChannel = Channel<InfoEvent>()
    val infoEvent = infoEventChannel.receiveAsFlow()

    /**
     * Event functions for the Info fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onAboutButtonClicked() = viewModelScope.launch {
        infoEventChannel.send(InfoEvent.NavigateToAboutScreen)
    }

    fun onCardListButtonClicked() = viewModelScope.launch {
        infoEventChannel.send(InfoEvent.NavigateToCardListScreen)
    }

    fun onRulesButtonClicked() = viewModelScope.launch {
        infoEventChannel.send(InfoEvent.NavigateToRulesScreen)
    }

    fun onBackButtonClicked() = viewModelScope.launch {
        infoEventChannel.send(InfoEvent.NavigateBackToHomeScreen)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class InfoEvent {
        object NavigateToAboutScreen : InfoEvent()
        object NavigateToCardListScreen : InfoEvent()
        object NavigateToRulesScreen : InfoEvent()
        object NavigateBackToHomeScreen : InfoEvent()
    }
}
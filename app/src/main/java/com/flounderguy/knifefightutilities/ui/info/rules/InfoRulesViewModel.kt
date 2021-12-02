package com.flounderguy.knifefightutilities.ui.info.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the InfoRulesFragment.
 * This ViewModel is in charge of:
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class InfoRulesViewModel @Inject constructor() : ViewModel() {

    // TODO: Add functionality to this ViewModel once its established what needs to be displayed here.

    /**
     * Event channel for RulesEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val rulesEventChannel = Channel<RulesEvent>()
    val rulesEvent = rulesEventChannel.receiveAsFlow()

    /**
     * Event functions for the Rules fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onBackButtonClicked() = viewModelScope.launch {
        rulesEventChannel.send(RulesEvent.NavigateBackToInfoScreen)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class RulesEvent {
        object NavigateBackToInfoScreen : RulesEvent()
    }
}
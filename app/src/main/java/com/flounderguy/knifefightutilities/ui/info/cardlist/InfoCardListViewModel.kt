package com.flounderguy.knifefightutilities.ui.info.cardlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the InfoCardListFragment.
 * This ViewModel is in charge of:
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class InfoCardListViewModel @Inject constructor() : ViewModel() {

    // TODO: Add functionality to this ViewModel once its established what needs to be displayed here.

    /**
     * Event channel for CardListEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val cardListEventChannel = Channel<CardListEvent>()
    val cardListEvent = cardListEventChannel.receiveAsFlow()

    /**
     * Event functions for the CardList fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onBackButtonClicked() = viewModelScope.launch {
        cardListEventChannel.send(CardListEvent.NavigateBackToInfoScreen)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class CardListEvent {
        object NavigateBackToInfoScreen : CardListEvent()
    }
}
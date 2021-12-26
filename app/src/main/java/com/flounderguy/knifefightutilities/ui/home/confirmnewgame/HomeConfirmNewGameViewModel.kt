package com.flounderguy.knifefightutilities.ui.home.confirmnewgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the HomeConfirmNewGameDialogFragment.
 * This ViewModel is in charge of:
 *      - Clearing all saved Gang data in case the user decides to start a New Game when an unfinished
 *          game exists.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class HomeConfirmNewGameViewModel @Inject constructor(
    private val repository: KnifeFightRepository
) : ViewModel() {

    /**
     * Event channel for NewGameEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val newGameEventChannel = Channel<NewGameEvent>()

    /**
     * Event functions for the NewGame dialog fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onConfirmClick() = viewModelScope.launch {
        repository.clearGangs()
        newGameEventChannel.send(NewGameEvent.NavigateToFirstStepScreen)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class NewGameEvent {
        object NavigateToFirstStepScreen : NewGameEvent()
    }
}
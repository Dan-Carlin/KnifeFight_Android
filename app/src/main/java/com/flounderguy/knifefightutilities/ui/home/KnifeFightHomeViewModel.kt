package com.flounderguy.knifefightutilities.ui.home

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import com.flounderguy.knifefightutilities.ui.settings.KnifeFightSettingsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the KnifeFightHomeFragment, entry point for the app.
 * This ViewModel is in charge of:
 *      - Checking the database for the existence of previous game data and notifying the fragment.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class KnifeFightHomeViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Variable to hold status for an active game
     */
    // This creates a variable to keep track of an active game state and sets it to a false value.
    private val _activeGame = MutableLiveData(false)
    val activeGame: LiveData<Boolean>
        get() = _activeGame

    // This method call updates _activeGame to true if there is a user Gang object in the database.
    init {
        checkIfActiveGameExists()
    }

    /**
     * Event channel for HomeEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val homeEventChannel = Channel<HomeEvent>()
    val homeEvent = homeEventChannel.receiveAsFlow()

    /**
     * Event functions for the Home fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onContinueGameClicked() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToGameToolsScreen)
    }

    fun onNewGameStarted() = viewModelScope.launch {
        // This splits the navigation path depending on the presence of an unfinished game.
        if (activeGame.value == true) {
            homeEventChannel.send(HomeEvent.NavigateToConfirmNewGameScreen)
        } else {
            // This clears the database anyways in the event of any unwanted lingering data.
            repository.clearGangs()
            homeEventChannel.send(HomeEvent.NavigateToFirstStepScreen)
        }
    }

    fun onInfoClicked() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToInfoScreen)
    }

    fun onSettingsClicked() = viewModelScope.launch {
        val previousPage = KnifeFightSettingsViewModel.NavigatedFrom.HOME
        state.set("previous_page", previousPage)
        homeEventChannel.send(HomeEvent.NavigateToSettingsScreen(previousPage))
    }

    /**
     * Private functions
     */
    // This is the method called when the ViewModel is initialized in order to update the _activeGame
    // variable.
    private fun checkIfActiveGameExists() = viewModelScope.launch {
        if (repository.userGangExists())
            _activeGame.value = true
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class HomeEvent {
        object NavigateToGameToolsScreen : HomeEvent()
        object NavigateToConfirmNewGameScreen : HomeEvent()
        object NavigateToFirstStepScreen : HomeEvent()
        object NavigateToInfoScreen : HomeEvent()
        data class NavigateToSettingsScreen
            (val previousPage: KnifeFightSettingsViewModel.NavigatedFrom) : HomeEvent()
    }
}
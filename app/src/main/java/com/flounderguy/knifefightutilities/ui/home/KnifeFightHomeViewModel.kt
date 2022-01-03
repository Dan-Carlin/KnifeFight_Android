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
     * Game state variable
     */
    // This variable keeps track of whether or not there is an active game in storage.
    private val _activeGame = MutableLiveData<Boolean>()
    val activeGame: LiveData<Boolean>
        get() = _activeGame

    /**
     * Event channel variables
     */
    // These variables create a flow channel of the objects in the HomeEvent class.
    private val homeEventChannel = Channel<HomeEvent>()
    val homeEvent = homeEventChannel.receiveAsFlow()

    /**
     * Startup method
     */
    // This method performs actions that are needed every time the fragment is entered.
    fun onAppStarted() = viewModelScope.launch {
        if (repository.rivalGangsExist()) {
            _activeGame.value = true
        } else {
            _activeGame.value = false
            repository.clearGangs()
        }
    }

    /**
     * Action methods
     */
    // These are the action methods for buttons in the HomeFragment UI.
    fun onContinueGameClicked() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToGameToolsMenuScreen)
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
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class HomeEvent {
        object NavigateToGameToolsMenuScreen : HomeEvent()
        object NavigateToConfirmNewGameScreen : HomeEvent()
        object NavigateToFirstStepScreen : HomeEvent()
        object NavigateToInfoScreen : HomeEvent()
        data class NavigateToSettingsScreen
            (val previousPage: KnifeFightSettingsViewModel.NavigatedFrom) : HomeEvent()
    }
}
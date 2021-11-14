package com.flounderguy.knifefightutilities.ui.home

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KnifeFightHomeViewModel @Inject constructor(
    private val repository: KnifeFightRepository
) : ViewModel() {

    /**
     * Variables to hold status for an active game
     */
    private val _activeGame = MutableLiveData<Boolean>()
    val activeGame: LiveData<Boolean>
        get() = _activeGame

    // Set _activeGame to false and update if database contains a Gang object
    init {
        _activeGame.value = false
        checkIfActiveGameExists()
    }

    /**
     * Navigation event channel
     */
    private val homeEventChannel = Channel<HomeEvent>()
    val homeEvent = homeEventChannel.receiveAsFlow()

    /**
     * Navigation functions for KnifeFightHomeFragment
     */
    // TODO: Activate confirm New Game dialog when this function is called.
    fun onContinueGameClicked() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToGameToolsScreen)
    }

    fun onNewGameStarted() = viewModelScope.launch {
        repository.clearGangs()
        homeEventChannel.send(HomeEvent.NavigateToFirstStepScreen)
    }

    fun onInfoClicked() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToInfoScreen)
    }

    fun onSettingsClicked() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToSettingsScreen)
    }

    /**
     * Function to check database for existing Gang objects
     */
    private fun checkIfActiveGameExists() = viewModelScope.launch {
        if (repository.userGangExists())
            _activeGame.value = true
    }

    /**
     * Navigation events for KnifeFightHomeFragment
     */
    sealed class HomeEvent {
        object NavigateToConfirmNewGameScreen : HomeEvent()
        object NavigateToGameToolsScreen : HomeEvent()
        object NavigateToFirstStepScreen : HomeEvent()
        object NavigateToInfoScreen : HomeEvent()
        object NavigateToSettingsScreen : HomeEvent()
    }
}
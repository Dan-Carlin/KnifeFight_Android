package com.flounderguy.knifefightutilities.ui.game.tools

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.ui.settings.KnifeFightSettingsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameToolsMenuViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Active tool variables
     */
    private val _howToUseDialogHasShown = MutableLiveData(false)
    val howToUseDialogHasShown: LiveData<Boolean>
        get() = _howToUseDialogHasShown

    private val _attackModeIsActive = MutableLiveData(false)
    val attackModeIsActive: LiveData<Boolean>
        get() = _attackModeIsActive

    private val _counterattackModeIsActive = MutableLiveData(false)
    val counterattackModeIsActive: LiveData<Boolean>
        get() = _counterattackModeIsActive

    private val _characterInfoIsActive = MutableLiveData(false)
    val characterInfoIsActive: LiveData<Boolean>
        get() = _characterInfoIsActive

    private val _settingsPageIsActive = MutableLiveData(false)
    val settingsPageIsActive: LiveData<Boolean>
        get() = _settingsPageIsActive

    private val _secondWindIsActive = MutableLiveData(false)
    val secondWindIsActive: LiveData<Boolean>
        get() = _secondWindIsActive

    /**
     * Event channel variables
     */
    // These variables create a flow channel of the objects in the sealed event class.
    private val gameToolsMenuEventChannel = Channel<GameToolsEvent>()
    val gameToolsMenuEvent = gameToolsMenuEventChannel.receiveAsFlow()

    /**
     * Startup method
     */
    // This method performs actions that are needed every time the fragment is entered.
    fun onGameToolsStarted() = viewModelScope.launch {
        _howToUseDialogHasShown.value = true
        gameToolsMenuEventChannel.send(GameToolsEvent.ShowHowToUseDialog)
    }

    /**
     * Action methods
     */
    // These are the action methods for buttons in the fragment's UI.
    fun onAttackModeClicked() = viewModelScope.launch {
        _attackModeIsActive.value = true
        gameToolsMenuEventChannel.send(GameToolsEvent.NavigateToAttackModeScreen)
    }

    fun onCounterAttackClicked() = viewModelScope.launch {
        _counterattackModeIsActive.value = true
        gameToolsMenuEventChannel.send(GameToolsEvent.NavigateToCounterAttackScreen)
    }

    fun onCharacterInfoClicked() = viewModelScope.launch {
        _characterInfoIsActive.value = true
        gameToolsMenuEventChannel.send(GameToolsEvent.NavigateToCharacterInfoScreen)
    }

    fun onSettingsClicked() = viewModelScope.launch {
        _settingsPageIsActive.value = true
        val previousPage = KnifeFightSettingsViewModel.NavigatedFrom.TOOLS
        state.set("previous_page", previousPage)
        gameToolsMenuEventChannel.send(GameToolsEvent.NavigateToSettingsScreen(previousPage))
    }

    fun onExitGameClicked() = viewModelScope.launch {
        gameToolsMenuEventChannel.send(GameToolsEvent.NavigateToEndGameConfirmScreen)
    }

    fun onBannerModeClicked() = viewModelScope.launch {
        gameToolsMenuEventChannel.send(GameToolsEvent.NavigateToPlayerBannerScreen)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class GameToolsEvent {
        object ShowHowToUseDialog : GameToolsEvent()
        object NavigateToAttackModeScreen : GameToolsEvent()
        object NavigateToCounterAttackScreen : GameToolsEvent()
        object NavigateToCharacterInfoScreen : GameToolsEvent()
        data class NavigateToSettingsScreen
            (val previousPage: KnifeFightSettingsViewModel.NavigatedFrom) : GameToolsEvent()
        object NavigateToEndGameConfirmScreen : GameToolsEvent()
        object NavigateToPlayerBannerScreen : GameToolsEvent()
        object NavigateToSecondWindScreen : GameToolsEvent()
    }
}


package com.flounderguy.knifefightutilities.ui.settings

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the KnifeFightSettingsFragment.
 * This ViewModel is in charge of:
 *      - The business logic to change the settings that determine how the app operates.
 *      - Notifying the fragment which page navigated to it.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class KnifeFightSettingsViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    // TODO: Implement a settings manager that this ViewModel can interact with.

    /**
     * Enum class and variable to identify previous page
     */
    enum class NavigatedFrom {
        HOME,
        TOOLS
    }

    // This fetches the value passed in from the previous fragment in order to identify itself.
    private val _previousPage = MutableLiveData<NavigatedFrom>(state.get("previous_page"))
    val previousPage: LiveData<NavigatedFrom>
        get() = _previousPage

    /**
     * Event channel for SettingsEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val settingsEventChannel = Channel<SettingsEvent>()
    val settingsEvent = settingsEventChannel.receiveAsFlow()

    /**
     * Event functions for the Settings fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onBackButtonClicked() = viewModelScope.launch {
        when (previousPage.value) {
            NavigatedFrom.HOME -> settingsEventChannel.send(SettingsEvent.NavigateBackToHomeScreen)
            NavigatedFrom.TOOLS -> settingsEventChannel.send(SettingsEvent.NavigateBackToToolsScreen)
        }
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class SettingsEvent {
        object NavigateBackToToolsScreen : SettingsEvent()
        object NavigateBackToHomeScreen : SettingsEvent()
    }
}
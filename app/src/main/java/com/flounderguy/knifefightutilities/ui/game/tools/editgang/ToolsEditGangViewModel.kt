package com.flounderguy.knifefightutilities.ui.game.tools.editgang

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the ToolsEditGangFragment.
 * This ViewModel is in charge of:
 *      - Providing an enum class that represents different themes to alter the look of the Gang name.
 *      - Providing an enum class that represents background art options for the Tools and Banner
 *          fragments.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class ToolsEditGangViewModel @Inject constructor() : ViewModel() {

    // TODO: Make enum classes for Gang name and background theme once art and fonts are established.

    /**
     * Event channel for EditGangEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val editGangEventChannel = Channel<EditGangEvent>()
    val editGangEvent = editGangEventChannel.receiveAsFlow()

    /**
     * Event functions for the EditGang fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onCloseButtonClicked() = viewModelScope.launch {
        editGangEventChannel.send(EditGangEvent.NavigateBackToGameToolsMenuScreen)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class EditGangEvent {
        object NavigateBackToGameToolsMenuScreen : EditGangEvent()
    }
}
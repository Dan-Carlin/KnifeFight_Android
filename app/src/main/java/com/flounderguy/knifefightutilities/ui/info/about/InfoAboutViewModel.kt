package com.flounderguy.knifefightutilities.ui.info.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the InfoAboutFragment.
 * This ViewModel is in charge of:
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class InfoAboutViewModel @Inject constructor() : ViewModel() {

    // TODO: Add functionality to this ViewModel once its established what needs to be displayed here.

    /**
     * Event channel for AboutEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val aboutEventChannel = Channel<AboutEvent>()
    val aboutEvent = aboutEventChannel.receiveAsFlow()

    /**
     * Event functions for the About fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onWebsiteButtonClicked() = viewModelScope.launch {
        aboutEventChannel.send(AboutEvent.OpenLinkToWebsite)
    }

    fun onFacebookButtonClicked() = viewModelScope.launch {
        aboutEventChannel.send(AboutEvent.OpenLinkToFacebook)
    }

    fun onInstagramButtonClicked() = viewModelScope.launch {
        aboutEventChannel.send(AboutEvent.OpenLinkToInstagram)
    }

    fun onTwitterButtonClicked() = viewModelScope.launch {
        aboutEventChannel.send(AboutEvent.OpenLinkToTwitter)
    }

    fun onBackButtonClicked() = viewModelScope.launch {
        aboutEventChannel.send(AboutEvent.NavigateBackToInfoScreen)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class AboutEvent {
        object OpenLinkToWebsite : AboutEvent()
        object OpenLinkToFacebook : AboutEvent()
        object OpenLinkToInstagram : AboutEvent()
        object OpenLinkToTwitter : AboutEvent()
        object NavigateBackToInfoScreen : AboutEvent()
    }
}
package com.flounderguy.knifefightutilities.ui.setup.firststep

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the SetupFirstStepFragment.
 * This ViewModel is in charge of:
 *      - Taking input from the user for a gang name, or generating one if needed.
 *      - Saving this value for the Gang object creation in the third step.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class SetupFirstStepViewModel @Inject constructor(
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Name value for the user Gang object
     */
    // This takes the name input by the user and saves it for the Gang object creation in the third step.
    var gangName = state.get<String>("name") ?: ""
        set(value) {
            field = value
            state.set("name", value)
        }

    /**
     * Event channel for FirstStepEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val firstStepEventChannel = Channel<FirstStepEvent>()
    val firstStepEvent = firstStepEventChannel.receiveAsFlow()

    /**
     * Event functions for the FirstStep fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onCancelButtonPressed() = viewModelScope.launch {
        firstStepEventChannel.send(FirstStepEvent.NavigateBackToHomeScreen)
    }

    fun onFirstStepCompleted() = viewModelScope.launch {
        firstStepEventChannel.send(FirstStepEvent.NavigateToSecondStepScreen(gangName))
    }

    /**
     * Name generator function
     */
    // This generates a randomized gangName string for the user.
    fun generateGangName(): String {
        val gangPrefix = listOf(
            "Southside", "Northside", "Eastside", "Westside", "12th Street",
            "18th Street", "Barrio", "Chinatown", "Downtown", "Uptown",
            "10th Avenue", "22nd Avenue", "Crazy Town", "East Coast", "West Coast",
            "Main Street", "Underground", "Hood", "The", "Dark", "Savage", "Wild",
            "Latin", "Ghost Town", "Los", "Humpty", "Notorious", "Cowbell",
            "Crazy", "Crusty", "Vegan", "Curmudgeonly", "Iron", "Saucy", "Good ol'",
            "Band of", "Big City", "Small Town", "Bloodthirsty", "Twisted",
            "River City", "Lake City", "Beachside", "Back Alley", "Country"
        )

        val gangSuffix = listOf(
            "Locos", "Clan", "Brotherhood", "Syndicate", "Ryders", "Family",
            "Familia", "Fancymen", "Clowns", "Cartel", "Mafia", "Boyz",
            "Nation", "Dawgz", "Jokerz", "Horsemen", "Militia", "Mob", "Gangstaz",
            "Hoodlums", "Crew", "Rapscallions", "Gang", "Scallywags", "Lunatics",
            "Ticklers", "Fiends", "Bruisers", "Hooligans", "Vermin", "Pirates",
            "Scumbags", "Sisterhood", "Warthogs", "Squad", "Soldiers",
            "Funnymen", "Animals", "Ladies Men", "Maids", "Poppers", "Homeslices",
            "Metalheads", "Ex-Girlfriends", "Ex-Boyfriends", "Zombies", "Lads"
        )
        return gangPrefix.random() + " " + gangSuffix.random()
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class FirstStepEvent {
        object NavigateBackToHomeScreen : FirstStepEvent()
        data class NavigateToSecondStepScreen(val name: String) : FirstStepEvent()
    }
}
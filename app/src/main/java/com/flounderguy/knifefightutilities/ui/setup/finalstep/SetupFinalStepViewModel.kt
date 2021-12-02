package com.flounderguy.knifefightutilities.ui.setup.finalstep

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the SetupFinalStepFragment.
 * This ViewModel is in charge of:
 *      - Creating Gang objects based on trait selections made by the user and saving them in the
 *          database.
 *      - Notifying the fragment which trait belongs to the user.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class SetupFinalStepViewModel @Inject constructor(
    private val repository: KnifeFightRepository
) : ViewModel() {

    // This populates the _userTrait upon initialization.
    init {
        getUserTrait()
    }

    /**
     * User trait variable
     */
    // This creates a variable to hold the trait selected in the third step.
    private val _userTrait = MutableLiveData<Gang.Trait>()
    val userTrait: LiveData<Gang.Trait>
        get() = _userTrait

    /**
     * Rival Gang lists
     */
    // This creates a list of rival Gang objects to be populated and inserted into the database.
    private val rivalGangList = mutableListOf<Gang>()
    // This creates a list of rival Gang traits based on user selections.
    private val rivalGangTraits = mutableListOf<Gang.Trait>()

    /**
     * Event channel for FinalStepEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val finalStepEventChannel = Channel<FinalStepEvent>()
    val finalStepEvent = finalStepEventChannel.receiveAsFlow()

    /**
     * Event functions for the FinalStep fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onSetupCompleted() = viewModelScope.launch {
        // This populates the list of Gang objects and inserts them into the database.
        populateRivalGangList()
        for (gang in rivalGangList) {
            repository.insertGang(gang)
        }
        finalStepEventChannel.send(FinalStepEvent.NavigateToGameToolsScreen)
    }

    /**
     * Select/deselect function for character trait buttons
     */
    // This adds the trait to the rivalGangTraits list if it is selected and removes it when deselected.
    fun onTraitSelected(trait: Gang.Trait, isSelected: Boolean) {
        if (isSelected) {
            rivalGangTraits.add(trait)
        } else {
            rivalGangTraits.remove(trait)
        }
    }

    /**
     * Private functions
     */
    // This fetches the trait from the database and is called during initialization of the page.
    private fun getUserTrait() = viewModelScope.launch {
        _userTrait.value.let { repository.getGangTrait() }
    }

    // This takes each trait in the list of traits selected by the user and wraps it in a Gang object
    // with default values. It is called when exiting the page.
    private fun populateRivalGangList() {
        for (trait in rivalGangTraits) {
            val newRivalGang = Gang("", Gang.GangColor.NONE, trait, isUser = false, isDefeated = false)
            rivalGangList.add(newRivalGang)
        }
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class FinalStepEvent {
        object NavigateToGameToolsScreen : FinalStepEvent()
    }
}
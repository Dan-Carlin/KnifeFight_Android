package com.flounderguy.knifefightutilities.ui.setup.finalstep

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
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
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * User gang variables
     */
    // This creates variables to hold the user gang values.
    private val _rivalsAreSelected = MutableLiveData(false)
    val rivalsAreSelected: LiveData<Boolean>
        get() = _rivalsAreSelected

    private var userGang = state.get<Gang>("gang")

    private val _userGangName = MutableLiveData(userGang?.name)
    val userGangName: LiveData<String?>
        get() = _userGangName

    private val _userGangColor = MutableLiveData(userGang?.color)
    val userGangColor: LiveData<Gang.Color?>
        get() = _userGangColor

    private val _userGangTrait = MutableLiveData(userGang?.trait)
    val userGangTrait: LiveData<Gang.Trait?>
        get() = _userGangTrait

    /**
     * Stat holder for the user's character trait
     */
    // This provides a handle for the user's trait stats.
    private val traitData = userGangTrait.value?.let { repository.getTraitFlow(it).asLiveData() }

    /**
     * Trait list
     */
    // This holds a list of all traits.
    val traitList = repository.getTraitList().asLiveData()

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
    fun onPreviousStepButtonClicked() = viewModelScope.launch {
        finalStepEventChannel.send(FinalStepEvent.NavigateBackToThirdStep)
    }

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
    fun onTraitSelected(trait: CharacterTrait) {
        val traitLabel = enumValueOf<Gang.Trait>(trait.name.uppercase().replace('-','_'))
        if (rivalGangTraits.contains(traitLabel)) {
            rivalGangTraits.remove(traitLabel)
        } else {
            rivalGangTraits.add(traitLabel)
        }
        _rivalsAreSelected.value = rivalGangTraits.isNotEmpty()
    }

    fun isUserTrait(trait: CharacterTrait): Boolean {
        return userGangTrait.value?.asString == trait.name
    }

    /**
     * Private functions
     */
    // This takes each trait in the list of traits selected by the user and wraps it in a Gang object
    // with default values. It is called when exiting the page.
    private fun populateRivalGangList() {
        for (trait in rivalGangTraits) {
            val newRivalGang = Gang("", Gang.Color.NONE, trait, isUser = false, isDefeated = false)
            rivalGangList.add(newRivalGang)
        }
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class FinalStepEvent {
        object NavigateBackToThirdStep : FinalStepEvent()
        object NavigateToGameToolsScreen : FinalStepEvent()
    }
}
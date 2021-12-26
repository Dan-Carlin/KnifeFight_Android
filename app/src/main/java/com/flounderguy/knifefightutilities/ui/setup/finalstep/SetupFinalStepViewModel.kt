package com.flounderguy.knifefightutilities.ui.setup.finalstep

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.Gang.Trait
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import com.flounderguy.knifefightutilities.util.convertTraitToLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the SetupFinalStepFragment.
 * This ViewModel is in charge of:
 *      - Taking input from the user for the gang trait cards that all opponents drew.
 *      - Using all input gathered to create and store a Gang object for each rival.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class SetupFinalStepViewModel @Inject constructor(
    private val repository: KnifeFightRepository
) : ViewModel() {

    /**
     * User Gang values
     */
    // These variables hold the flow of the user Gang trait data.
    private val userGangFlow = repository.getUserGang()
    var userTrait = Trait.NONE

    /**
     * Trait values
     */
    // This tells the fragment if a trait has been clicked.
    private val _rivalsAreSelected = MutableLiveData(false)
    val rivalsAreSelected: LiveData<Boolean>
        get() = _rivalsAreSelected

    // This holds a list of all CharacterTrait objects in the database.
    val traitList = repository.getTraitList().asLiveData()

    /**
     * Rival Gang lists
     */
    // This creates a list of rival Gang objects to be populated and inserted into the database.
    private val _rivalGangList = mutableListOf<Gang>()
    val rivalGangList: List<Gang>
        get() = _rivalGangList

    // This creates a list of rival Trait values based on user selections.
    private val _rivalGangTraits = mutableListOf<Trait>()
    val rivalGangTraits: List<Trait>
        get() = _rivalGangTraits

    /**
     * Event channel variables
     */
    // These variables create a flow channel of the objects in the FinalStepEvent class.
    private val finalStepEventChannel = Channel<FinalStepEvent>()
    val finalStepEvent = finalStepEventChannel.receiveAsFlow()

    /**
     * Startup method
     */
    // This method performs actions that are needed every time the fragment is entered.
    fun onFinalStepStarted() = viewModelScope.launch {
        userGangFlow.collect {
            userTrait = it.trait!!
        }
    }

    /**
     * Action methods
     */
    // These are the action methods for buttons in the FinalStepFragment UI.
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
     * Setter method
     */
    // This method adds Trait values into rivalGangTraits if they were not previously in there.
    fun onTraitSelected(trait: CharacterTrait) {
        val traitLabel = convertTraitToLabel(trait)
        if (rivalGangTraits.contains(traitLabel)) {
            _rivalGangTraits.remove(traitLabel)
        } else {
            _rivalGangTraits.add(traitLabel)
        }
        _rivalsAreSelected.value = rivalGangTraits.isNotEmpty()
    }

    /**
     * Public method
     */
    // This compares the CharacterTrait value against the Trait value for the user Gang.
    fun isUserTrait(trait: CharacterTrait): Boolean {
        return userTrait.asString == trait.name
    }

    /**
     * Private method
     */
    // This takes each Trait in rivalGangTraits and wraps it in a Gang object with default values.
    private fun populateRivalGangList() {
        for (trait in rivalGangTraits) {
            val newRivalGang = Gang("", Gang.Color.NONE, trait, isUser = false, isDefeated = false)
            _rivalGangList.add(newRivalGang)
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
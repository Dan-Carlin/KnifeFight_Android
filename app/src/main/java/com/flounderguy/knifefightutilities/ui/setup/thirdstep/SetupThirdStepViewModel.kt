package com.flounderguy.knifefightutilities.ui.setup.thirdstep

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.Gang.Color
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
 * ViewModel for the SetupThirdStepFragment.
 * This ViewModel is in charge of:
 *      - Taking input from the user for the gang trait card that they drew.
 *      - Using all input gathered to create and store a Gang object for the user.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class SetupThirdStepViewModel @Inject constructor(
    private val repository: KnifeFightRepository
) : ViewModel() {

    /**
     * All values for the user Gang object
     */
    // These variables hold the flow of user Gang data if there is a Gang object in the database.
    private val userGangFlow = repository.getUserGang()
    private val _userGang = MutableLiveData<Gang>()
    val userGang: LiveData<Gang>
        get() = _userGang

    // These variables hold the values emitted by userGangFlow, or default values if there are none.
    private var gangName = userGang.value?.name ?: ""
    var gangColor = userGang.value?.color ?: Color.NONE
    var gangTrait = userGang.value?.trait ?: Trait.NONE
        set(value) {
            field = value
            if (value != Trait.NONE) {
                _traitIsSelected.value = true
            }
        }

    /**
     * Trait values
     */
    // This tells the fragment if a trait has been clicked.
    private val _traitIsSelected = MutableLiveData(false)
    val traitIsSelected: LiveData<Boolean>
        get() = _traitIsSelected

    // This holds a list of all CharacterTrait objects in the database.
    val traitList = repository.getTraitList().asLiveData()

    /**
     * Event channel variables
     */
    // These variables create a flow channel of the objects in the ThirdStepEvent class.
    private val thirdStepEventChannel = Channel<ThirdStepEvent>()
    val thirdStepEvent = thirdStepEventChannel.receiveAsFlow()

    /**
     * Startup method
     */
    // This method performs actions that are needed every time the fragment is entered.
    fun onThirdStepStarted() = viewModelScope.launch {
        if (repository.userGangExists()) {
            userGangFlow.collect {
                _userGang.value = it
                gangName = it.name
                gangColor = it.color!!
                gangTrait = it.trait!!
            }
        }
    }

    /**
     * Action methods
     */
    // These are the action methods for buttons in the ThirdStepFragment UI.
    fun onPreviousStepButtonClicked() = viewModelScope.launch {
        thirdStepEventChannel.send(ThirdStepEvent.NavigateBackToSecondStep)
    }

    fun onThirdStepCompleted() = viewModelScope.launch {
        val updatedGang = userGang.value!!.copy(
            name = gangName,
            color = gangColor,
            trait = gangTrait,
            isUser = true,
            isDefeated = false
        )
        repository.updateGang(updatedGang)
        thirdStepEventChannel.send(ThirdStepEvent.NavigateToFinalStepScreen)
    }

    /**
     * Setter method
     */
    // This method sets the value of the trait variable by converting a CharacterTrait to a Gang.Trait enum.
    fun setUserTrait(trait: CharacterTrait) {
        gangTrait = convertTraitToLabel(trait)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class ThirdStepEvent {
        object NavigateBackToSecondStep : ThirdStepEvent()
        object NavigateToFinalStepScreen : ThirdStepEvent()
    }
}
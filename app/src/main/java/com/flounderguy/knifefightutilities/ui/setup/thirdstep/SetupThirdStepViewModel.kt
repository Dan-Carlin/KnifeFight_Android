package com.flounderguy.knifefightutilities.ui.setup.thirdstep

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * All values for the user Gang object
     */
    // This fetches the values from the last two steps and creates the final one necessary for the
    // user Gang.
    private val _traitIsSelected = MutableLiveData(false)
    val traitIsSelected: LiveData<Boolean>
        get() = _traitIsSelected

    val gang = state.get<Gang>("gang")

    val gangName = state.get<String>("name") ?: gang?.name
    val gangColor = state.get<Gang.Color>("color") ?: gang?.color
    var gangTrait = gang?.trait ?: Gang.Trait.NONE
        set(value) {
            field = value
            state.set("trait", value)
            if (value != Gang.Trait.NONE) {
                _traitIsSelected.value = true
            }
        }

    /**
     * Trait list
     */
    // This holds a list of all traits.
    val traitList = repository.getTraitList().asLiveData()

    /**
     * Event channel for ThirdStepEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val thirdStepEventChannel = Channel<ThirdStepEvent>()
    val thirdStepEvent = thirdStepEventChannel.receiveAsFlow()

    /**
     * Event functions for the ThirdStep fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onPreviousStepButtonClicked() = viewModelScope.launch {
        thirdStepEventChannel.send(ThirdStepEvent.NavigateBackToSecondStep)
    }

    fun onThirdStepCompleted() = viewModelScope.launch {
        // This performs a null check on the variables from the last two steps and uses all the input
        // gathered to create a user Gang object to store in the database.
        if (gang != null) {
            val updatedGang = gang.copy(
                name = gangName,
                color = gangColor,
                trait = gangTrait,
                isUser = true,
                isDefeated = false
            )
            repository.updateGang(updatedGang)
        } else {
            if (gangName != null && gangColor != null) {
                val newGang =
                    Gang(gangName, gangColor, gangTrait, isUser = true, isDefeated = false)
                state.set("gang", newGang)
                createUserGang(newGang)
                thirdStepEventChannel.send(ThirdStepEvent.NavigateToFinalStepScreen(newGang))
            }
        }
    }

    fun setUserTrait(trait: CharacterTrait) {
        gangTrait = enumValueOf(trait.name.uppercase().replace('-', '_'))
    }

    /**
     * Private functions
     */
    // This stores the user Gang in the database.
    private fun createUserGang(gang: Gang) = viewModelScope.launch {
        repository.insertGang(gang)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class ThirdStepEvent {
        object NavigateBackToSecondStep : ThirdStepEvent()
        data class NavigateToFinalStepScreen(val gang: Gang) : ThirdStepEvent()
    }
}
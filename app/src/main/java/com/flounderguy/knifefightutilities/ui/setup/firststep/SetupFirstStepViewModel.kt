package com.flounderguy.knifefightutilities.ui.setup.firststep

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import com.flounderguy.knifefightutilities.data.Gang.Color
import com.flounderguy.knifefightutilities.data.Gang.Trait
import com.flounderguy.knifefightutilities.util.generateGangName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the SetupFirstStepFragment.
 * This ViewModel is in charge of:
 *      - Receiving a flow of user Gang values and storing them if they exist (and holding defaults
 *          if they don't).
 *      - Either:
 *          a. Creating a new user Gang object with the name input and default values.
 *          b. Updating the name of an existing user Gang object.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class SetupFirstStepViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
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
    var gangName = userGang.value?.name ?: ""
    var gangColor = userGang.value?.color ?: Color.NONE
        set(value) {
            field = value
            state.set("color", value)
        }
    private var gangTrait = userGang.value?.trait ?: Trait.NONE

    /**
     * Event channel variables
     */
    // These variables create a flow channel of the objects in the sealed event class.
    private val firstStepEventChannel = Channel<FirstStepEvent>()
    val firstStepEvent = firstStepEventChannel.receiveAsFlow()

    /**
     * Startup method
     */
    // This method performs actions that are needed every time the fragment is entered.
    fun onFirstStepStarted() = viewModelScope.launch {
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
    // These are the action methods for buttons in the fragment's UI.
    fun onCancelButtonPressed() = viewModelScope.launch {
        firstStepEventChannel.send(FirstStepEvent.NavigateBackToHomeScreen)
    }

    fun onFirstStepCompleted() = viewModelScope.launch {
        if (userGang.value != null) {
            val updatedGang = userGang.value!!.copy(
                name = gangName,
                color = gangColor,
                trait = gangTrait,
                isUser = true,
                isDefeated = false
            )
            repository.updateGang(updatedGang)
        } else {
            val newGang = Gang(gangName, gangColor, gangTrait, isUser = true, isDefeated = false)
            repository.insertGang(newGang)
        }
        firstStepEventChannel.send(FirstStepEvent.NavigateToSecondStepScreen(gangColor))
    }

    // This generates a randomized gangName string for the user.
    fun onGenerateNameButtonPressed(): String {
        return generateGangName()
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class FirstStepEvent {
        object NavigateBackToHomeScreen : FirstStepEvent()
        data class NavigateToSecondStepScreen(val color: Color) : FirstStepEvent()
    }
}
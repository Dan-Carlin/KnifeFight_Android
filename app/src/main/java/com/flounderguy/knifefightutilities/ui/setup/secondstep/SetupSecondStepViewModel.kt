package com.flounderguy.knifefightutilities.ui.setup.secondstep

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.Gang.Color
import com.flounderguy.knifefightutilities.data.Gang.Trait
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the SetupSecondStepFragment.
 * This ViewModel is in charge of:
 *      - Taking input from the user for a gang color selection.
 *      - Saving this value for the Gang object creation in the third step.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class SetupSecondStepViewModel @Inject constructor(
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
    private var gangName = userGang.value?.name ?: ""
    var gangColor = state.get<Color>("color") ?: userGang.value?.color ?: Color.NONE
        set(value) {
            field = value
            if (value != Color.NONE) {
                _colorIsSelected.value = true
            }
        }
    var gangTrait = userGang.value?.trait ?: Trait.NONE
        set(value) {
            field = value
            state.set("trait", value)
        }

    /**
     * Color values
     */
    // This tells the fragment if a color has been clicked.
    private val _colorIsSelected = MutableLiveData(false)
    val colorIsSelected: LiveData<Boolean>
        get() = _colorIsSelected

    // This holds an array of all items in the Gang.Color enum class.
    private val _colorArray = MutableLiveData(Color.values())
    val colorArray: LiveData<Array<Color>>
        get() = _colorArray

    /**
     * Event channel variables
     */
    // These variables create a flow channel of the objects in the sealed event class.
    private val secondStepEventChannel = Channel<SecondStepEvent>()
    val secondStepEvent = secondStepEventChannel.receiveAsFlow()

    /**
     * Startup method
     */
    // This method performs actions that are needed every time the fragment is entered.
    fun onSecondStepStarted() = viewModelScope.launch {
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
    fun onPreviousStepButtonClicked() = viewModelScope.launch {
        updateGang()
        secondStepEventChannel.send(SecondStepEvent.NavigateBackToFirstStep)
    }

    fun onSecondStepCompleted() = viewModelScope.launch {
        updateGang()
        secondStepEventChannel.send(SecondStepEvent.NavigateToThirdStepScreen(gangTrait))
    }

    /**
     * Public method
     */
    // This compares the color value input against the Color value for the user Gang.
    fun isUserColor(color: Color): Boolean {
        return gangColor.name == color.name
    }

    /**
     * Private method
     */
    // This method updates the gang with the new values set by the user.
    private fun updateGang() = viewModelScope.launch {
        val updatedGang = userGang.value!!.copy(
            name = gangName,
            color = gangColor,
            trait = gangTrait,
            isUser = true,
            isDefeated = false
        )
        repository.updateGang(updatedGang)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class SecondStepEvent {
        object NavigateBackToFirstStep : SecondStepEvent()
        data class NavigateToThirdStepScreen(val trait: Trait) : SecondStepEvent()
    }
}
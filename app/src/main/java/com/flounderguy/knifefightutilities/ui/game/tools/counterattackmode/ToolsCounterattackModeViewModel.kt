package com.flounderguy.knifefightutilities.ui.game.tools.counterattackmode

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import com.flounderguy.knifefightutilities.util.DieType
import com.flounderguy.knifefightutilities.util.ResultRequest
import com.flounderguy.knifefightutilities.util.rollDice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolsCounterattackModeViewModel @Inject constructor(
    private val repository: KnifeFightRepository
) : ViewModel() {

    /**
     * Variables to hold the user Gang object and trait from the database
     */
    private val userGangFlow = repository.getUserGang()
    val userGang = userGangFlow.asLiveData()

    private val _userGangTrait = MutableLiveData<Gang.Trait>(userGang.value?.trait)
    private val userGangTrait: LiveData<Gang.Trait>
        get() = _userGangTrait

    /**
     * Stat holder for the user's character trait
     */
    private val traitData = userGangTrait.value?.let { repository.getTraitFlow(it).asLiveData() }

    /**
     * Variables to hold the result of the counterattack roll and damage modifier
     */
    private val _counterAttackOutput = MutableLiveData<Int>()
    private val counterAttackOutput: LiveData<Int>
        get() = _counterAttackOutput

    private val _damageModifier = MutableLiveData<Int>(traitData?.value?.damage)
    private val damageModifier: LiveData<Int>
        get() = _damageModifier

    /**
     * Navigation event channels for CounterattackMode
     */
    private val counterattackModeEventChannel = Channel<CounterattackModeEvent>()
    val counterattackModeEvent = counterattackModeEventChannel.receiveAsFlow()

    /**
     * Event functions for the Counterattack Mode fragment
     */
    private fun onCounterattackRoll(damage: Int, modifier: Int) = viewModelScope.launch {
        counterattackModeEventChannel.send(
            CounterattackModeEvent.ShowResultsMessage(damage, modifier, ResultRequest.COUNTER)
        )
    }

    fun onCloseButtonClicked() = viewModelScope.launch {
        counterattackModeEventChannel.send(CounterattackModeEvent.NavigateBackToGameToolsMenuScreen)
    }

    /**
     * Gameplay functions
     */
    fun rollForCounterAttack() {
        _counterAttackOutput.value = rollDice(DieType.D4)
        checkCounterForNullAndNavigate()
    }

    /**
     * Private functions for Counterattack Mode calculations
     */
    private fun checkCounterForNullAndNavigate() {
        if (counterAttackOutput.value != null && damageModifier.value != null) {
            onCounterattackRoll(counterAttackOutput.value!!, damageModifier.value!!)
        }
    }

    /**
     * Navigation events for CounterattackMode fragment
     * */
    sealed class CounterattackModeEvent {
        data class ShowResultsMessage(
            val output: Int,
            val modifier: Int,
            val request: ResultRequest
        ) : CounterattackModeEvent()

        object NavigateBackToGameToolsMenuScreen : CounterattackModeEvent()
    }
}
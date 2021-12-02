package com.flounderguy.knifefightutilities.ui.game.tools.attackmode

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
class ToolsAttackModeViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    init {
        attackValueChecks()
    }

    /**
     * Variables to hold the user and rival Gang objects from the database
     */
    private val userGangFlow = repository.getUserGang()
    val userGang = userGangFlow.asLiveData()

    private val _userGangColor = MutableLiveData<Gang.GangColor>(userGang.value?.color)
    val userGangColor: LiveData<Gang.GangColor>
        get() = _userGangColor

    private val _userGangTrait = MutableLiveData<Gang.Trait>(userGang.value?.trait)
    val userGangTrait: LiveData<Gang.Trait>
        get() = _userGangTrait

    private val rivalGangsFlow = repository.getRivalGangs()
    val rivalGangs = rivalGangsFlow.asLiveData()

    /**
     * Stat holder for the user's character trait
     */
    private val traitData = userGangTrait.value?.let { repository.getTraitFlow(it).asLiveData() }

    /**
     * Variables for attack mode operations
     */
    // Attack/Damage output variables
    private val _attackOutput = MutableLiveData<Int>()
    val attackOutput: LiveData<Int>
        get() = _attackOutput

    private val _damageOutput = MutableLiveData<Int>()
    val damageOutput: LiveData<Int>
        get() = _damageOutput

    // User stat variables
    private val _attackModifier = MutableLiveData<Int>(traitData?.value?.attack)
    val attackModifier: LiveData<Int>
        get() = _attackModifier

    private val _damageModifier = MutableLiveData<Int>(traitData?.value?.damage)
    val damageModifier: LiveData<Int>
        get() = _damageModifier

    private val _luckModifier = MutableLiveData<Int>(traitData?.value?.luck)
    val luckModifier: LiveData<Int>
        get() = _luckModifier

    private val _attackResults = MutableLiveData<Int>(state.get("attack_result"))
    val attackResults: LiveData<Int>
        get() = _attackResults

    private val _damageResults = MutableLiveData<Int>(state.get("damage_result"))
    val damageResults: LiveData<Int>
        get() = _damageResults

    // Rival stat variables
    private val _rivalGuts = MutableLiveData<Int>()
    val rivalGuts: LiveData<Int>
        get() = _rivalGuts

    // Attack check variables
    private val _isCriticalHit = MutableLiveData(false)
    val isCriticalHit: LiveData<Boolean>
        get() = _isCriticalHit

    private val _attackIsSuccessful = MutableLiveData(false)
    val attackIsSuccessful: LiveData<Boolean>
        get() = _attackIsSuccessful

    private val _attackIsTied = MutableLiveData(false)
    val attackIsTied: LiveData<Boolean>
        get() = _attackIsTied

    // Weapon depleted variables
    private val _d6IsDepleted = MutableLiveData(false)
    val d6IsDepleted: LiveData<Boolean>
        get() = _d6IsDepleted

    private val _d8IsDepleted = MutableLiveData(false)
    val d8IsDepleted: LiveData<Boolean>
        get() = _d8IsDepleted

    private val _d10IsDepleted = MutableLiveData(false)
    val d10IsDepleted: LiveData<Boolean>
        get() = _d10IsDepleted

    /**
     * Navigation event channels for AttackMode
     */
    private val attackModeEventChannel = Channel<AttackModeEvent>()
    val attackModeEvent = attackModeEventChannel.receiveAsFlow()

    /**
     * Event functions for the Attack Mode fragment
     */
    private fun onAttackRoll(attack: Int, modifier: Int) = viewModelScope.launch {
        attackModeEventChannel.send(
            AttackModeEvent.ShowResultsMessage(attack, modifier, ResultRequest.ATTACK)
        )
    }

    private fun onDamageRoll(damage: Int, modifier: Int) = viewModelScope.launch {
        attackModeEventChannel.send(
            AttackModeEvent.ShowResultsMessage(damage, modifier, ResultRequest.DAMAGE)
        )
    }

    fun onFinishButtonClicked() = viewModelScope.launch {
        attackModeEventChannel.send(AttackModeEvent.NavigateBackToToolsScreen)
    }

    fun onCloseButtonClicked() = viewModelScope.launch {
        if (attackIsSuccessful.value == true) {
            attackModeEventChannel.send(AttackModeEvent.ShowConfirmQuitAttackMessage)
        } else {
            attackModeEventChannel.send(AttackModeEvent.NavigateBackToToolsScreen)
        }
    }

    /**
     * Gameplay functions
     */
    // TODO: Make a filter that hides the rivals that have already been defeated.
    fun onRivalSelect(rivalGang: Gang) = viewModelScope.launch {
        _rivalGuts.value = rivalGang.let { repository.getGuts(it.trait) }!!
    }

    fun rollForAttackOrDamage(dieType: DieType) {
        when (dieType) {
            DieType.D4 -> {
                _damageOutput.value = rollDice(DieType.D4)
                checkDamageForNullAndNavigate()
            }
            DieType.D6 -> {
                _damageOutput.value = rollDice(DieType.D6)
                _d6IsDepleted.value = true
                checkDamageForNullAndNavigate()
            }
            DieType.D8 -> {
                _damageOutput.value = rollDice(DieType.D8)
                _d8IsDepleted.value = true
                checkDamageForNullAndNavigate()
            }
            DieType.D10 -> {
                _damageOutput.value = rollDice(DieType.D10)
                _d10IsDepleted.value = true
                checkDamageForNullAndNavigate()
            }
            DieType.D20 -> {
                _attackOutput.value = rollDice(DieType.D20)
                checkAttackForNullAndNavigate()
            }
        }
    }

    /**
     * Private functions for Attack Mode calculations
     */
    private fun checkAttackForNullAndNavigate() {
        if (attackOutput.value != null && attackModifier.value != null) {
            onAttackRoll(attackOutput.value!!, attackModifier.value!!)
        }
    }

    private fun checkDamageForNullAndNavigate() {
        if (damageOutput.value != null && damageModifier.value != null) {
            onDamageRoll(damageOutput.value!!, damageModifier.value!!)
        }
    }

    private fun attackValueChecks() {
        if (attackOutput.value != null && rivalGuts.value != null && luckModifier.value != null) {

            _isCriticalHit.value =
                attackOutput.value!! >= (DieType.D20.sides - luckModifier.value!!)
            _attackIsSuccessful.value = attackResults.value!! > rivalGuts.value!!
            _attackIsTied.value = attackResults.value == rivalGuts.value
        }
    }

    /**
     * Navigation events for AttackMode fragment
     */
    sealed class AttackModeEvent {
        data class ShowResultsMessage(
            val output: Int,
            val modifier: Int,
            val request: ResultRequest
        ) : AttackModeEvent()

        object ShowConfirmQuitAttackMessage : AttackModeEvent()
        object NavigateBackToToolsScreen : AttackModeEvent()
    }
}
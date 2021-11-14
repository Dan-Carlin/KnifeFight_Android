package com.flounderguy.knifefightutilities.ui.gametools.attackmode

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class GameToolsAttackModeViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    enum class DieType(val sides: Int) {
        D4(4),
        D6(6),
        D8(8),
        D10(10),
        D20(20)
    }

    /**
     * Variables to hold the user and rival Gang objects from the database
     */
    private val userGang = state.get<LiveData<Gang>>("userGang")

    // TODO: Implement Recyclerview that displays this list of Gangs as thumbnail images
    private val rivalGangs = state.get<LiveData<List<Gang>>>("rivalGangs")

    private var attackValue = 0
    private var damageValue = 0
    private var attackModifier = 0
    private var damageModifier = 0

    init {
        calculateModifiers()
    }

    /**
     * LiveData for the character trait
     */
    private val gangTrait = state.get<Gang.Trait>("trait")
    private val traitData = getTrait()?.asLiveData()

    private val attackModeEventChannel = Channel<AttackModeEvent>()
    val attackModeEvent = attackModeEventChannel.receiveAsFlow()

    private fun calculateModifiers() {
        traitData?.value?.attack?.let { attackModifier = it }
        traitData?.value?.damage?.let { damageModifier = it }
    }

    private fun rollDice(dieType: DieType): Int {
        return (1..dieType.sides).random()
    }

    private fun attackRoll() {
        attackValue = rollDice(DieType.D20) + attackModifier
    }

    private fun damageRoll(dieType: DieType) {
        damageValue = rollDice(dieType) + damageModifier
    }

    private fun getTrait(): Flow<CharacterTrait>? =
        gangTrait?.let { repository.getTraitFlow(it) }

    sealed class AttackModeEvent {
        data class ShowAttackBonusMessage(val msg: String) : AttackModeEvent()
        data class ShowDamageBonusMessage(val msg: String) : AttackModeEvent()
    }
}
package com.flounderguy.knifefightutilities.ui.game.tools.basemode

import android.util.Log
import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolsBaseModeViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
): ViewModel() {

    /**
     * Flows to emit values for the user and rival Gang objects from the database
     */
    // Gang object is split up into the three observable values that we can work with.
    private val rivalGangsFlow = repository.getRivalGangs()
    val rivalGangs = rivalGangsFlow.asLiveData()

    private val userGangFlow = repository.getUserGang()
    val userGang = userGangFlow.asLiveData()

    private val traitList = repository.getTraitList()

    /**
     * Variables to hold starting HP and any additional HP that might be added on by character trait
     */
    private val _hpCount = MutableLiveData<Int>()
    val hpCount: LiveData<Int>
        get() = _hpCount

    private val _hasModifiedHp = MutableLiveData<Boolean>()
    val hasModifiedHp: LiveData<Boolean>
        get() = _hasModifiedHp

    private val _hpModifier = MutableLiveData<Int>()
    val hpModifier: LiveData<Int>
        get() = _hpModifier

    private val _secondWindIsSuccessful = MutableLiveData<Boolean>(state.get("second_wind_result"))
    val secondWindIsSuccessful: LiveData<Boolean>
        get() = _secondWindIsSuccessful

    /**
     * Action Methods
     */
    fun onPlusHpClicked() {
        _hpCount.value.let {
            if (it != null)
                _hpCount.value = it + 1
        }
    }

    fun onMinusHpClicked() {
        _hpCount.value.let {
            if (it != null && it > 0) {
                _hpCount.value = it - 1
            } else {
                onHpDepleted()
            }
        }
    }

    fun onRivalSelect(rivalGang: Gang, isChecked: Boolean) {
        toggleRivalDefeatStatus(rivalGang, isChecked)
    }

    /**
     * Private Methods
     */
    private fun setHpValues() = viewModelScope.launch {
        traitList.collect { traits ->
            userGangFlow.collect { gang ->
                val userTrait = traits.first { it.name == gang.trait?.asString }

                _hpModifier.value = userTrait.hp
                _hasModifiedHp.value = hpModifier.value != 0
                if (hpModifier.value != null) {
                    _hpCount.value = STARTING_HP + hpModifier.value!!
                }
            }
        }
    }

    private fun toggleRivalDefeatStatus(rivalGang: Gang, isChecked: Boolean) = viewModelScope.launch {
        rivalGang.trait?.let { Log.d("BaseVM-DefeatBug", it.asString + "is being updated with isDefeated = " + isChecked) }
        repository.updateGang(rivalGang.copy(isDefeated = isChecked))
    }

    private fun onHpDepleted() = viewModelScope.launch {
//        _secondWindIsActive.value = true
//        gameToolsMenuEventChannel.send(GameToolsMenuViewModel.GameToolsEvent.NavigateToSecondWindScreen)
    }

    private fun onSuccessfulSecondWindRoll() {
        if (secondWindIsSuccessful.value == true) {
            _hpCount.value = STARTING_HP + hpModifier.value!!
        }
    }

    companion object {
        const val STARTING_HP = 25
    }
}
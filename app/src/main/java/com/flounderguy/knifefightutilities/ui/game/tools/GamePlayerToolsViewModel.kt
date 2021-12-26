package com.flounderguy.knifefightutilities.ui.game.tools

import android.util.Log
import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import com.flounderguy.knifefightutilities.ui.settings.KnifeFightSettingsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamePlayerToolsViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Flows to emit values for the user and rival Gang objects from the database
     */
    // Gang object is split up into the three observable values that we can work with.
    private val rivalGangsFlow = repository.getRivalGangs()
    val rivalGangs = rivalGangsFlow.asLiveData()

    private val userGangNameFlow = repository.getGangNameFlow()
    val userGangName = userGangNameFlow.asLiveData()

    private val userGangColorFlow = repository.getGangColorFlow()
    val userGangColor = userGangColorFlow.asLiveData()

    private val userGangTraitFlow = repository.getGangTraitFlow()
    val userGangTrait = userGangTraitFlow.asLiveData()

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

    init {
        setHpValues()

//        if (secondWindIsSuccessful.value != null) {
//            onSuccessfulSecondWindRoll()
//        }
    }

    /**
     * Variables to check which tool is currently active
     */
    private val _attackModeIsActive = MutableLiveData(false)
    val attackModeIsActive: LiveData<Boolean>
        get() = _attackModeIsActive

    private val _counterattackModeIsActive = MutableLiveData(false)
    val counterattackModeIsActive: LiveData<Boolean>
        get() = _counterattackModeIsActive

    private val _secondWindIsActive = MutableLiveData(false)
    val secondWindIsActive: LiveData<Boolean>
        get() = _secondWindIsActive

    private val _secondWindIsSuccessful = MutableLiveData<Boolean>(state.get("second_wind_result"))
    val secondWindIsSuccessful: LiveData<Boolean>
        get() = _secondWindIsSuccessful

    /**
     * Navigation event channel for GameTools
     */
    private val gameToolsEventChannel = Channel<GameToolsEvent>()
    val gameToolsEvent = gameToolsEventChannel.receiveAsFlow()

    /**
     * Navigation functions for the GameTools fragment
     */
    fun onGameToolsStarted() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.ShowHowToUseDialog)
    }

    fun onAttackModeClicked() = viewModelScope.launch {
        _attackModeIsActive.value = true
        gameToolsEventChannel.send(GameToolsEvent.NavigateToAttackModeScreen)
    }

    fun onCounterAttackClicked() = viewModelScope.launch {
        _counterattackModeIsActive.value = true
        gameToolsEventChannel.send(GameToolsEvent.NavigateToCounterAttackScreen)
    }

    fun onCharacterInfoClicked() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.NavigateToCharacterInfoScreen)
    }

    fun onEditGangClicked() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.NavigateToEditGangBannerScreen)
    }

    fun onSettingsClicked() = viewModelScope.launch {
        val previousPage = KnifeFightSettingsViewModel.NavigatedFrom.TOOLS
        state.set("previous_page", previousPage)
        gameToolsEventChannel.send(GameToolsEvent.NavigateToSettingsScreen(previousPage))
    }

    fun onExitGameClicked() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.NavigateToEndGameConfirmScreen)
    }

    fun onBannerModeClicked() = viewModelScope.launch {
        state.set("hp_count", hpCount.value)
        gameToolsEventChannel.send(GameToolsEvent.NavigateToPlayerBannerScreen(hpCount.value!!))
    }

    private fun onHpDepleted() = viewModelScope.launch {
        _secondWindIsActive.value = true
        gameToolsEventChannel.send(GameToolsEvent.NavigateToSecondWindScreen)
    }

    /**
     * Gameplay functions
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

    fun onRivalSelect(rivalGang: Gang, isDefeated: Boolean) {
        toggleRivalDefeatStatus(rivalGang, isDefeated)
    }

    private fun setHpValues() = viewModelScope.launch {
        traitList.collect { traits ->
            userGangTraitFlow.collect { traitLabel ->
                val userTrait = traits.first { it.name == traitLabel.asString }

                _hpModifier.value = userTrait.hp
                _hasModifiedHp.value = hpModifier.value != 0
                if (hpModifier.value != null) {
                    _hpCount.value = STARTING_HP + hpModifier.value!!
                }
            }
        }
    }

    private fun onSuccessfulSecondWindRoll() {
        if (secondWindIsSuccessful.value == true) {
            _hpCount.value = STARTING_HP + hpModifier.value!!
        }
    }

    private fun toggleRivalDefeatStatus(rivalGang: Gang, isDefeated: Boolean) = viewModelScope.launch {
        repository.updateGang(rivalGang.copy(isDefeated = !isDefeated))
    }

    /**
     * Navigation events for GameTools fragment
     * */
    sealed class GameToolsEvent {
        object ShowHowToUseDialog : GameToolsEvent()
        object NavigateToAttackModeScreen : GameToolsEvent()
        object NavigateToCounterAttackScreen : GameToolsEvent()
        object NavigateToCharacterInfoScreen : GameToolsEvent()
        object NavigateToEditGangBannerScreen : GameToolsEvent()
        data class NavigateToSettingsScreen
            (val previousPage: KnifeFightSettingsViewModel.NavigatedFrom) : GameToolsEvent()
        object NavigateToEndGameConfirmScreen : GameToolsEvent()
        data class NavigateToPlayerBannerScreen(val hpCount: Int) : GameToolsEvent()
        object NavigateToSecondWindScreen : GameToolsEvent()
    }

    companion object {
        const val STARTING_HP = 25
    }
}


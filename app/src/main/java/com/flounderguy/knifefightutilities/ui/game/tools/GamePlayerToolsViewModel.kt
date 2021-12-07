package com.flounderguy.knifefightutilities.ui.game.tools

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import com.flounderguy.knifefightutilities.ui.settings.KnifeFightSettingsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamePlayerToolsViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Variables to hold the user and rival Gang objects from the database
     */
    private val userGangFlow = repository.getUserGang()
    val userGang = userGangFlow.asLiveData()

    // Gang object is split up into the three observable values that we can work with.
    private val _userGangName = MutableLiveData<String>(userGang.value?.name)
    val userGangName: LiveData<String>
        get() = _userGangName

    private val _userGangColor = MutableLiveData<Gang.Color>(userGang.value?.color)
    val userGangColor: LiveData<Gang.Color>
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
     * Variables to hold starting HP and any additional HP that might be added on by character trait
     */
    private val _hpCount = MutableLiveData(STARTING_HP + hpModifier.value!!)
    val hpCount: LiveData<Int>
        get() = _hpCount

    private val _hasModifiedHp = MutableLiveData(hpModifier.value!! != 0)
    val hasModifiedHp: LiveData<Boolean>
        get() = _hasModifiedHp

    private val _hpModifier = MutableLiveData<Int>(traitData?.value?.hp)
    val hpModifier: LiveData<Int>
        get() = _hpModifier

    /**
     * Variables to check which tool is currently active
     */
    private val _attackModeIsActive = MutableLiveData<Boolean>()
    val attackModeIsActive: LiveData<Boolean>
        get() = _attackModeIsActive

    private val _counterattackModeIsActive = MutableLiveData<Boolean>()
    val counterattackModeIsActive: LiveData<Boolean>
        get() = _counterattackModeIsActive

    private val _secondWindIsActive = MutableLiveData<Boolean>()
    val secondWindIsActive: LiveData<Boolean>
        get() = _secondWindIsActive

    private val _secondWindIsSuccessful = MutableLiveData<Boolean>(state.get("second_wind_result"))
    val secondWindIsSuccessful: LiveData<Boolean>
        get() = _secondWindIsSuccessful


    init {
        _attackModeIsActive.value = false
        _counterattackModeIsActive.value = false
        _secondWindIsActive.value = false

        if (secondWindIsSuccessful.value != null) {
            onSuccessfulSecondWindRoll()
        }
    }

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


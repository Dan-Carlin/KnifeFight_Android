package com.flounderguy.knifefightutilities.ui.gametools

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KnifeFightGameToolsViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Variables to hold the user and rival Gang objects from the database
     */
    private lateinit var userGang: LiveData<Gang>

    // TODO: Implement Recyclerview that displays this list of Gangs as thumbnail images
    private lateinit var rivalGangs: LiveData<List<Gang>>

    /**
     * Variables to hold starting HP and any additional HP that might be added on by character trait
     */
    private val _hpCount = MutableLiveData<Int>()
    val hpCount: LiveData<Int>
        get() = _hpCount

    // Hp modifier will be calculated inside init block using the calculateHpModifier() method.
    private var hpModifier = 0

    init {
        loadUserGang()
        loadRivalGangs()
        calculateHpModifier()
        _hpCount.value = STARTING_HP + hpModifier
    }

    // Gang object is split up into the three observable values that we can work with.
    private val _userGangName = MutableLiveData<String>(userGang.value?.name)
    val userGangName: LiveData<String>
        get() = _userGangName

    private val _userGangColor = MutableLiveData<Gang.GangColor>(userGang.value?.color)
    val userGangColor: LiveData<Gang.GangColor>
        get() = _userGangColor

    private val _userGangTrait = MutableLiveData<Gang.Trait>(userGang.value?.trait)
    val userGangTrait: LiveData<Gang.Trait>
        get() = _userGangTrait

    /**
     * LiveData for the character trait
     */
    private val traitData = getTrait()?.asLiveData()

    /**
     * Navigation event channel for GameTools
     */
    private val gameToolsEventChannel = Channel<GameToolsEvent>()
    val gameToolsEvent = gameToolsEventChannel.receiveAsFlow()

    /**
     * Function activated upon entering fragment
     */
    fun onGameToolsStarted() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.ShowHowToUseDialog)
    }

    /**
     * Functions to increment HP up and down
     */
    fun onPlusHpClicked() {
        _hpCount.value.let {
            if (it != null)
                _hpCount.value = it + 1
        }
    }

    fun onMinusHpClicked() {
        _hpCount.value.let {
            if (it != null && it > 0)
                _hpCount.value = it - 1
        }
    }

    /**
     * Navigation functions for the GameTools fragment
     */
    fun onAttackModeClicked() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.NavigateToAttackModeScreen(userGang, rivalGangs))
    }

    fun onCharacterInfoClicked() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.NavigateToCharacterInfoScreen)
    }

    fun onEditGangBannerClicked() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.NavigateToEditGangBannerScreen)
    }

    fun onSettingsClicked() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.NavigateToSettingsScreen)
    }

    /**
     * Getters
     */
    private fun getTrait(): Flow<CharacterTrait>? =
        userGangTrait.value?.let { repository.getTraitFlow(it) }

    private fun loadUserGang() = viewModelScope.launch {
        userGang = repository.getUserGang().asLiveData()
        state.set("userGang", userGang)
    }

    private fun loadRivalGangs() = viewModelScope.launch {
        rivalGangs = repository.getRivalGangs().asLiveData()
        state.set("rivalGangs", rivalGangs)
    }

    // Updates hpModifier to reflect the hp stat of the users Trait
    private fun calculateHpModifier() {
        traitData?.value?.hp?.let { hpModifier = it }
    }

    /**
     * Navigation events for GameTools fragment
     * */
    sealed class GameToolsEvent {
        object ShowHowToUseDialog : GameToolsEvent()
        data class NavigateToAttackModeScreen(
            val userGang: LiveData<Gang>,
            val rivalGangs: LiveData<List<Gang>>
        ) : GameToolsEvent()
        object NavigateToCharacterInfoScreen : GameToolsEvent()
        object NavigateToEditGangBannerScreen : GameToolsEvent()
        object NavigateToSettingsScreen : GameToolsEvent()
    }

    companion object {
        const val STARTING_HP = 25
    }
}


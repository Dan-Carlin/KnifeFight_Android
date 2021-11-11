package com.flounderguy.knifefightutilities.ui.gametools

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.CharacterTraitDao
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.GangDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KnifeFightGameToolsViewModel @Inject constructor(
    private var startingHp: Int,
    private val gangDao: GangDao,
    private val characterTraitDao: CharacterTraitDao,
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Variables to hold starting HP and any additional HP that might be added on by character trait
     */
    var hpCount = startingHp

    // Hp modifier will be calculated when entering fragment using the calculateHpModifier() method.
    var hpModifier = 0

    /**
     * Variable to hold the Gang object that has been passed in
     */
    val gang = state.get<Gang>("gang")

    // Gang object is split up into the three values that we can work with.
    val gangName = state.get<String>("name") ?: gang?.name
    val gangColor = state.get<Gang.GangColor>("color") ?: gang?.color
    val traitLabel = state.get<Gang.Trait>("trait") ?: gang?.trait

    /**
     * Flow and LiveData for the character trait
     */
    val traitFlow = getTrait()
    val traitData = traitFlow.asLiveData()

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
        calculateHpModifier()
    }

    /**
     * Functions to increment HP up and down
     */
    fun onPlusHpClicked() {
        hpCount++
    }

    fun onMinusHpClicked() {
        if (hpCount > 0) {
            hpCount--
        }
    }

    /**
     * Navigation functions for the GameTools fragment
     */
    fun onAttackModeClicked() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.ShowAttackModeDialog(traitLabel!!))
    }

    fun onCharacterInfoClicked() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.ShowCharacterInfoDialog(traitLabel!!))
    }

    fun onEditGangBannerClicked() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.ShowEditGangBannerDialog(gangName!!, gangColor!!))
    }

    fun onSettingsClicked() = viewModelScope.launch {
        gameToolsEventChannel.send(GameToolsEvent.NavigateToSettingsScreen)
    }

    /**
     * Private functions to fetch trait data needed in GameTools
     */
    private fun calculateHpModifier() {
        hpModifier = traitData.value?.hp!!
    }

    private fun getTrait(): Flow<CharacterTrait> =
        characterTraitDao.getTraitByName(traitLabel!!.asString)

    /**
     * Navigation events for GameTools fragment
     * */
    sealed class GameToolsEvent {
        object ShowHowToUseDialog : GameToolsEvent()
        data class ShowAttackModeDialog(val trait: Gang.Trait) : GameToolsEvent()
        data class ShowCharacterInfoDialog(val trait: Gang.Trait) : GameToolsEvent()
        data class ShowEditGangBannerDialog(val name: String, val color: Gang.GangColor) :
            GameToolsEvent()

        object NavigateToSettingsScreen : GameToolsEvent()
    }
}
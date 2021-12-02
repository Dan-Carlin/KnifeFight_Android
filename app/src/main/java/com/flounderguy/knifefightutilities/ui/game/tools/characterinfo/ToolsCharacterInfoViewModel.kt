package com.flounderguy.knifefightutilities.ui.game.tools.characterinfo

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the ToolsCharacterInfoFragment.
 * This ViewModel is in charge of:
 *      - Retrieving key pieces of data from the database.
 *      - Splitting that data up into displayable components.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class ToolsCharacterInfoViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
) : ViewModel() {

    /**
     * Variables for User Gang data
     */
    // This retrieves the user Gang from the database and converts it to LiveData.
    private val userGangFlow = repository.getUserGang()
    val userGang = userGangFlow.asLiveData()

    // This creates a variable for the only component of the userGang needed, the trait.
    private val _userGangTrait = MutableLiveData<Gang.Trait>(userGang.value?.trait)
    private val userGangTrait: LiveData<Gang.Trait>
        get() = _userGangTrait

    /**
     * Stat holder for the user's character trait
     */
    // This provides a handle for the user's trait stats and character info.
    private val traitData = userGangTrait.value?.let { repository.getTraitFlow(it).asLiveData() }

    /**
     * Character trait stats and info text variables
     */
    // This uses the traitData holder and splits every component into its own variable.
    private val _traitName = MutableLiveData<String>(traitData?.value?.name)
    private val traitName: LiveData<String>
        get() = _traitName

    private val _traitWeapon = MutableLiveData<String>(traitData?.value?.weapon)
    private val traitWeapon: LiveData<String>
        get() = _traitWeapon

    private val _traitHpStat = MutableLiveData<Int>(traitData?.value?.hp)
    private val traitHpStat: LiveData<Int>
        get() = _traitHpStat

    private val _traitGutsStat = MutableLiveData<Int>(traitData?.value?.guts)
    private val traitGutsStat: LiveData<Int>
        get() = _traitGutsStat

    private val _traitAttackStat = MutableLiveData<Int>(traitData?.value?.attack)
    private val traitAttackStat: LiveData<Int>
        get() = _traitAttackStat

    private val _traitDamageStat = MutableLiveData<Int>(traitData?.value?.damage)
    private val traitDamageStat: LiveData<Int>
        get() = _traitDamageStat

    private val _traitLuckStat = MutableLiveData<Int>(traitData?.value?.luck)
    private val traitLuckStat: LiveData<Int>
        get() = _traitLuckStat

    private val _traitUtility = MutableLiveData<String>(traitData?.value?.utility)
    private val traitUtility: LiveData<String>
        get() = _traitUtility

    /**
     * Event channel for CharacterInfoEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val characterInfoEventChannel = Channel<CharacterInfoEvent>()
    val characterInfoEvent = characterInfoEventChannel.receiveAsFlow()

    /**
     * Event functions for the CharacterInfo fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onCloseButtonClicked() = viewModelScope.launch {
        characterInfoEventChannel.send(CharacterInfoEvent.NavigateBackToToolsScreen)
    }

    /**
     * Event List
     */
    // This creates a list of events that must be implemented at some point.
    sealed class CharacterInfoEvent {
        object NavigateBackToToolsScreen : CharacterInfoEvent()
    }
}
package com.flounderguy.knifefightutilities.ui.game.banner

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the GameBannerFragment.
 * This ViewModel is in charge of:
 *      - Retrieving key pieces of data from the database.
 *      - Splitting that data up into displayable components.
 *      - Receiving the hpCount from the Tools ViewModel and making it available to the fragment.
 *      - Providing navigation events for the fragment to implement.
 */
@HiltViewModel
class GameBannerViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    /**
     * Variables for User Gang data
     */
    // This retrieves the user Gang from the database and converts it to LiveData.
    private val userGangFlow = repository.getUserGang()
    val userGang = userGangFlow.asLiveData()

    // This splits the user Gang LiveData into its separate components: name, GangColor, and Trait.
    private val _userGangName = MutableLiveData<String>(userGang.value?.name)
    val userGangName: LiveData<String>
        get() = _userGangName

    private val _userGangColor = MutableLiveData<Gang.Color>(userGang.value?.color)
    val userGangColor: LiveData<Gang.Color>
        get() = _userGangColor

    private val _userGangTrait = MutableLiveData<Gang.Trait>(userGang.value?.trait)
    val userGangTrait: LiveData<Gang.Trait>
        get() = _userGangTrait

    /**
     * Stat holder for the user's character trait
     */
    // This provides a handle for the user's trait stats.
    private val traitData = userGangTrait.value?.let { repository.getTraitFlow(it).asLiveData() }

    /**
     * Variable for the remaining HP
     */
    // This stores the remaining HP value that was sent in from the Tools fragment via the
    // SavedStateHandle.
    private val _hpCount = MutableLiveData<Int>(state.get("hp_count"))
    val hpCount: LiveData<Int>
        get() = _hpCount

    /**
     * Character trait stats
     */
    // This uses the traitData holder and splits the stats into their own variables.
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

    /**
     * Event channel for BannerEvents
     */
    // This creates a channel for all events associated with this ViewModel's corresponding fragment.
    private val bannerEventChannel = Channel<BannerEvent>()
    val bannerEvent = bannerEventChannel.receiveAsFlow()

    /**
     * Event functions for the PlayerBanner fragment
     */
    // This creates functions that can be called from the fragment to execute each event.
    fun onToolsModeClicked() = viewModelScope.launch {
        bannerEventChannel.send(BannerEvent.NavigateBackToGameToolsMenuScreen)
    }

    /**
     * Event list
     */
    // This creates a list of events that must be implemented at some point.
    sealed class BannerEvent {
        object NavigateBackToGameToolsMenuScreen : BannerEvent()
    }
}
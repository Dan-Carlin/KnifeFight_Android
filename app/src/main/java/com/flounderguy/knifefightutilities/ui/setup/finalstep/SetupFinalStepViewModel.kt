package com.flounderguy.knifefightutilities.ui.setup.finalstep

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupFinalStepViewModel @Inject constructor(
    private val repository: KnifeFightRepository
) : ViewModel() {

    init {
        getUserTrait()
    }

    /**
     * Variables that hold the trait chosen in the previous step as observable LiveData
     */
    private val _userTrait = MutableLiveData<Gang.Trait>()
    val userTrait: LiveData<Gang.Trait>
        get() = _userTrait

    /**
     * Lists that hold the selected rival Gang traits and the Gang objects to be added to db
     */
    private val rivalGangList = mutableListOf<Gang>()
    private val rivalGangTraits = mutableListOf<Gang.Trait>()

    /**
     * Navigation event channel
     */
    private val finalStepEventChannel = Channel<FinalStepEvent>()
    val finalStepEvent = finalStepEventChannel.receiveAsFlow()

    /**
     * Navigation function for SetupFinalStepFragment
     */
    fun onSetupCompleted() = viewModelScope.launch {
        populateRivalGangList()
        for (gang in rivalGangList) {
            repository.insertGang(gang)
        }
        finalStepEventChannel.send(FinalStepEvent.NavigateToGameToolsScreen)
    }

    /**
     * Select/deselect functions for character trait buttons
     */
    fun onTraitSelected(trait: Gang.Trait) = rivalGangTraits.add(trait)

    fun onTraitDeselected(trait: Gang.Trait) = rivalGangTraits.remove(trait)

    /**
     * Functions to set user trait value and populate rival gang list using the list of traits
     */
    private fun getUserTrait() = viewModelScope.launch {
        _userTrait.value.let { repository.getGangTrait() }
    }

    private fun populateRivalGangList() {
        for (trait in rivalGangTraits) {
            val newRivalGang = Gang("", Gang.GangColor.NONE, trait, isUser = false, isDefeated = false)
            rivalGangList.add(newRivalGang)
        }
    }

    /**
     * Navigation event for SetupFinalStepFragment
     */
    sealed class FinalStepEvent {
        object NavigateToGameToolsScreen : FinalStepEvent()
    }
}
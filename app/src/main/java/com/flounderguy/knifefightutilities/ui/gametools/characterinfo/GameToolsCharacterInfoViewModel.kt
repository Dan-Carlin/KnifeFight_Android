package com.flounderguy.knifefightutilities.ui.gametools.characterinfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameToolsCharacterInfoViewModel @Inject constructor(
    private val repository: KnifeFightRepository,
    private val state: SavedStateHandle
) : ViewModel() {
}
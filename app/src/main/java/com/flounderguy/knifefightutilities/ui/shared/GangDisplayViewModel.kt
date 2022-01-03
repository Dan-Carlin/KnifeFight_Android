package com.flounderguy.knifefightutilities.ui.shared

import androidx.lifecycle.*
import com.flounderguy.knifefightutilities.data.KnifeFightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the GangDisplayFragment.
 * This ViewModel is in charge of:
 *      - Providing a user Gang flow with values for the display fragment.
 *      - Telling the display fragment whether a user Gang is stored.
 */
@HiltViewModel
class GangDisplayViewModel @Inject constructor(
    private val repository: KnifeFightRepository
) : ViewModel() {

    /**
     * All values for the user Gang object
     */
    // This variable holds the flow of user Gang data if there is a Gang object in the database.
    val userGangFlow = repository.getUserGang()

    // This holds a boolean value of whether a user Gang has been created yet or not.
    private val _userGangExists = MutableLiveData(false)
    val userGangExists: LiveData<Boolean>
        get() = _userGangExists

    init {
        checkForUserGang()
    }

    /**
     * Private methods
     */
    // This checks the database for a user Gang.
    private fun checkForUserGang() = viewModelScope.launch {
        if (repository.userGangExists())
            _userGangExists.value = true
    }
}
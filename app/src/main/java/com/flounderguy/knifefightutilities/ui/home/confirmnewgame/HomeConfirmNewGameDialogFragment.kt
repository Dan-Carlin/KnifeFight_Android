package com.flounderguy.knifefightutilities.ui.home.confirmnewgame

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.ui.home.KnifeFightHomeFragmentDirections
import com.flounderguy.knifefightutilities.ui.home.KnifeFightHomeViewModel
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeConfirmNewGameDialogFragment : DialogFragment() {

    private val newGameViewModel: HomeConfirmNewGameViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            newGameViewModel.newGameEvent.collect { event ->
                when (event) {
                    is HomeConfirmNewGameViewModel.NewGameEvent.NavigateToFirstStepScreen -> {
                        val actionConfirmNewGameToSetup =
                            HomeConfirmNewGameDialogFragmentDirections.actionHomeConfirmNewGameDialogFragmentToSetupFirstStepFragment()
                        findNavController().navigate(actionConfirmNewGameToSetup)
                    }
                }.exhaustive
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Confirm new game")
            .setMessage("There is an unfinished game, continue with new game? (Previous game data will be deleted.)")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Start New Game") { _, _ ->
                newGameViewModel.onConfirmClick()
            }
            .create()
    }



}
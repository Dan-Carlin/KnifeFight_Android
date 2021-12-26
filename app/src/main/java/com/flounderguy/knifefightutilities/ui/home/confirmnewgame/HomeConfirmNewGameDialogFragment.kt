package com.flounderguy.knifefightutilities.ui.home.confirmnewgame

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * HomeConfirmNewGameDialogFragment is a dialog encountered when starting a game during active game state.
 * This fragment is in charge of:
 *      - Blocking setup and notifying the user if there is an active game saved in the database.
 *      - Navigating to first step of setup if positive action is taken by the user.
 */
@AndroidEntryPoint
class HomeConfirmNewGameDialogFragment : DialogFragment() {

    /**
     * Variables
     */
    // This creates an instance of the viewModel for this fragment.
    private val newGameViewModel: HomeConfirmNewGameViewModel by viewModels()

    /**
     * Dialog lifecycle method
     */
    // This executes all the code needed to create this dialog.
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm new game")
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setMessage("There is an unfinished game, continue with new game setup? (Previous game data will be deleted.)")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Start New Game") { _, _ ->
                newGameViewModel.onConfirmClick()
                val action =
                    HomeConfirmNewGameDialogFragmentDirections.actionHomeConfirmNewGameDialogFragmentToSetupFirstStepFragment()
                findNavController().navigate(action)
            }
            .create()
}
package com.flounderguy.knifefightutilities.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.KnifeFightFragmentHomeBinding
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * KnifeFightHomeFragment is the entry point of the app.
 * This fragment is in charge of:
 *      - Displaying a menu of all top level features in the app.
 *      - Displaying an additional "Continue Game" button depending on the current game state.
 */
@AndroidEntryPoint
class KnifeFightHomeFragment : Fragment(R.layout.knife_fight_fragment_home) {

    /**
     * Variables
     */
    // This creates an instance of the viewModel for this fragment.
    private val homeViewModel: KnifeFightHomeViewModel by viewModels()

    // ViewBinding variable
    private lateinit var homeBinding: KnifeFightFragmentHomeBinding

    /**
     * Lifecycle methods
     */
    // This executes all the code that should run upon creation of this fragment.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewBinding variable
        homeBinding = KnifeFightFragmentHomeBinding.bind(view)

        // Button actions
        homeBinding.apply {
            buttonContinueFightHome.setOnClickListener {
                homeViewModel.onContinueGameClicked()
            }
            buttonStartFightHome.setOnClickListener {
                homeViewModel.onNewGameStarted()
            }
            buttonInfoHome.setOnClickListener {
                homeViewModel.onInfoClicked()
            }
            buttonSettingsHome.setOnClickListener {
                homeViewModel.onSettingsClicked()
            }
        }

        // Event channel implementation
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.homeEvent.collect { event ->
                when (event) {
                    is KnifeFightHomeViewModel.HomeEvent.NavigateToGameToolsMenuScreen -> {
                        val actionHomeToGameTools =
                            KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToGameToolsMenuFragment()
                        findNavController().navigate(actionHomeToGameTools)
                    }
                    is KnifeFightHomeViewModel.HomeEvent.NavigateToConfirmNewGameScreen -> {
                        val actionConfirmNewGameDialog =
                            KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToHomeConfirmNewGameDialogFragment()
                        findNavController().navigate(actionConfirmNewGameDialog)
                    }
                    is KnifeFightHomeViewModel.HomeEvent.NavigateToFirstStepScreen -> {
                        val actionHomeToSetup =
                            KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToSetupFirstStepFragment()
                        findNavController().navigate(actionHomeToSetup)
                    }
                    is KnifeFightHomeViewModel.HomeEvent.NavigateToInfoScreen -> {
                        val actionHomeToInfo =
                            KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToKnifeFightInfoFragment()
                        findNavController().navigate(actionHomeToInfo)
                    }
                    is KnifeFightHomeViewModel.HomeEvent.NavigateToSettingsScreen -> {
                        val actionHomeToSettings =
                            KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToKnifeFightSettingsFragment(
                                event.previousPage
                            )
                        findNavController().navigate(actionHomeToSettings)
                    }
                }.exhaustive
            }
        }
    }

    // This executes the code that should run every time the fragment is entered.
    override fun onStart() {
        super.onStart()
        // ViewBinding variable
        homeBinding = KnifeFightFragmentHomeBinding.bind(requireView())

        // UI initialization and ViewModel interaction
        homeViewModel.onAppStarted()

        homeViewModel.activeGame.observe(viewLifecycleOwner) {
            homeBinding.buttonContinueFightHome.isVisible = it
        }
    }
}
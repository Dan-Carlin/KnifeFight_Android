package com.flounderguy.knifefightutilities.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.KnifeFightFragmentHomeBinding
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class KnifeFightHomeFragment : Fragment(R.layout.knife_fight_fragment_home) {

    private val homeViewModel: KnifeFightHomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeBinding = KnifeFightFragmentHomeBinding.bind(view)

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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.homeEvent.collect { event ->
                when (event) {
                    is KnifeFightHomeViewModel.HomeEvent.NavigateToConfirmNewGameScreen -> {
                        val actionHomeToConfirmNewGame =
                            KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToHomeConfirmNewGameDialogFragment()
                        findNavController().navigate(actionHomeToConfirmNewGame)
                    }
                    is KnifeFightHomeViewModel.HomeEvent.NavigateToGameToolsScreen -> {
                        val actionHomeToGameTools =
                            KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToGamePlayerToolsFragment()
                        findNavController().navigate(actionHomeToGameTools)
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
}
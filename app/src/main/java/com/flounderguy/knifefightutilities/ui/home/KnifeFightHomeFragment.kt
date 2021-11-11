package com.flounderguy.knifefightutilities.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.KnifeFightFragmentHomeBinding
import com.flounderguy.knifefightutilities.ui.setup.KnifeFightSetupViewModel
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class KnifeFightHomeFragment : Fragment(R.layout.knife_fight_fragment_home) {

    private val setupViewModel: KnifeFightSetupViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeBinding = KnifeFightFragmentHomeBinding.bind(view)

        homeBinding.apply {
            buttonStartFightHome.setOnClickListener {
                setupViewModel.onNewGameStarted()
            }
            buttonInfoHome.setOnClickListener {
                setupViewModel.onInfoClicked()
            }
            buttonSettingsHome.setOnClickListener {
                setupViewModel.onSettingsClicked()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            setupViewModel.homeEvent.collect { event ->
                when (event) {
                    is KnifeFightSetupViewModel.HomeEvent.NavigateToFirstStepScreen -> {
                        val actionHomeToSetup =
                            KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToSetupFirstStepFragment()
                        findNavController().navigate(actionHomeToSetup)
                    }
                    is KnifeFightSetupViewModel.HomeEvent.NavigateToInfoScreen -> {
                        val actionHomeToInfo =
                            KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToKnifeFightInfoFragment()
                        findNavController().navigate(actionHomeToInfo)
                    }
                    is KnifeFightSetupViewModel.HomeEvent.NavigateToSettingsScreen -> {
                        val actionHomeToSettings =
                            KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToKnifeFightSettingsFragment()
                        findNavController().navigate(actionHomeToSettings)
                    }
                }.exhaustive
            }
        }
    }
}
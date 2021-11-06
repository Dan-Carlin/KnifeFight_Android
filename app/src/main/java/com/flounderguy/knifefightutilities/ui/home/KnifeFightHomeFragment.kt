package com.flounderguy.knifefightutilities.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.KnifeFightFragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KnifeFightHomeFragment : Fragment(R.layout.knife_fight_fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeBinding = KnifeFightFragmentHomeBinding.bind(view)

        homeBinding.apply {

            buttonStartFightHome.setOnClickListener {
                val actionHomeToSetup =
                    KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToSetupFirstStepFragment()
                findNavController().navigate(actionHomeToSetup)
            }

            buttonInfoHome.setOnClickListener {
                val actionHomeToInfo =
                    KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToKnifeFightInfoFragment()
                findNavController().navigate(actionHomeToInfo)
            }

            buttonSettingsHome.setOnClickListener {
                val actionHomeToSettings =
                    KnifeFightHomeFragmentDirections.actionKnifeFightHomeFragmentToKnifeFightSettingsFragment()
                findNavController().navigate(actionHomeToSettings)
            }
        }
    }
}
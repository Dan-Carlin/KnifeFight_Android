package com.flounderguy.knifefightutilities.ui.setup.thirdstep

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.SetupFragmentThirdStepBinding
import com.flounderguy.knifefightutilities.ui.setup.KnifeFightSetupViewModel
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SetupThirdStepFragment : Fragment(R.layout.setup_fragment_third_step) {

    private val setupViewModel: KnifeFightSetupViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val thirdStepBinding = SetupFragmentThirdStepBinding.bind(view)

        thirdStepBinding.apply {
            buttonFinishSetup.setOnClickListener {
                setupViewModel.onSetupCompleted()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            setupViewModel.thirdStepEvent.collect { event ->
                when (event) {
                    is KnifeFightSetupViewModel.ThirdStepEvent.NavigateToGameToolsScreen -> {
                        val actionThirdStepToGameTools =
                            SetupThirdStepFragmentDirections.actionSetupThirdStepFragmentToKnifeFightGameToolsFragment(
                                event.gang
                            )
                        findNavController().navigate(actionThirdStepToGameTools)
                    }
                }.exhaustive
            }
        }
    }
}
package com.flounderguy.knifefightutilities.ui.setup.firststep

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.SetupFragmentFirstStepBinding
import com.flounderguy.knifefightutilities.ui.setup.KnifeFightSetupViewModel
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SetupFirstStepFragment : Fragment(R.layout.setup_fragment_first_step) {

    private val setupViewModel: KnifeFightSetupViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstStepBinding = SetupFragmentFirstStepBinding.bind(view)

        firstStepBinding.apply {
            buttonNextStepSetup.setOnClickListener {
                setupViewModel.onFirstStepCompleted()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            setupViewModel.firstStepEvent.collect { event ->
                when (event) {
                    is KnifeFightSetupViewModel.FirstStepEvent.NavigateToSecondStepScreen -> {
                        val actionFirstStepToSecondStep =
                            SetupFirstStepFragmentDirections.actionSetupFirstStepFragmentToSetupSecondStepFragment()
                        findNavController().navigate(actionFirstStepToSecondStep)
                    }
                }.exhaustive
            }
        }
    }
}
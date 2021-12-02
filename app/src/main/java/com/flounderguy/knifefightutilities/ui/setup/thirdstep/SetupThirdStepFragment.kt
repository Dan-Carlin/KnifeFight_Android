package com.flounderguy.knifefightutilities.ui.setup.thirdstep

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.SetupFragmentThirdStepBinding
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SetupThirdStepFragment : Fragment(R.layout.setup_fragment_third_step) {

    private val thirdStepViewModel: SetupThirdStepViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val thirdStepBinding = SetupFragmentThirdStepBinding.bind(view)

        thirdStepBinding.apply {
            buttonNextStepSetup.setOnClickListener {
                thirdStepViewModel.onThirdStepCompleted()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            thirdStepViewModel.thirdStepEvent.collect { event ->
                when (event) {
                    is SetupThirdStepViewModel.ThirdStepEvent.NavigateToFinalStepScreen -> {
                        val actionThirdStepToFinalStep =
                            SetupThirdStepFragmentDirections.actionSetupThirdStepFragmentToSetupFinalStepFragment()
                        findNavController().navigate(actionThirdStepToFinalStep)
                    }
                }.exhaustive
            }
        }
    }
}
package com.flounderguy.knifefightutilities.ui.setup.secondstep

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.SetupFragmentSecondStepBinding
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SetupSecondStepFragment : Fragment(R.layout.setup_fragment_second_step) {

    private val secondStepViewModel: SetupSecondStepViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val secondStepBinding = SetupFragmentSecondStepBinding.bind(view)

        secondStepBinding.apply {
            buttonNextStepSetup.setOnClickListener {
                secondStepViewModel.onSecondStepCompleted()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            secondStepViewModel.secondStepEvent.collect { event ->
                when (event) {
                    is SetupSecondStepViewModel.SecondStepEvent.NavigateToThirdStepScreen -> {
                        val actionSecondStepToThirdStep =
                            SetupSecondStepFragmentDirections.actionSetupSecondStepFragmentToSetupThirdStepFragment(
                                event.color
                            )
                        findNavController().navigate(actionSecondStepToThirdStep)
                    }
                }.exhaustive
            }
        }
    }
}
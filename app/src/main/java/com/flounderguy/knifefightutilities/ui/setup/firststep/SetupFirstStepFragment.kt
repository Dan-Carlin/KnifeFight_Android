package com.flounderguy.knifefightutilities.ui.setup.firststep

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.SetupFragmentFirstStepBinding
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SetupFirstStepFragment : Fragment(R.layout.setup_fragment_first_step) {

    private val firstStepViewModel: SetupFirstStepViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstStepBinding = SetupFragmentFirstStepBinding.bind(view)

        firstStepBinding.apply {
            buttonNextStepSetup.setOnClickListener {
                firstStepViewModel.onFirstStepCompleted()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            firstStepViewModel.firstStepEvent.collect { event ->
                when (event) {
                    is SetupFirstStepViewModel.FirstStepEvent.NavigateToSecondStepScreen -> {
                        val actionFirstStepToSecondStep =
                            SetupFirstStepFragmentDirections.actionSetupFirstStepFragmentToSetupSecondStepFragment(
                                event.name
                            )
                        findNavController().navigate(actionFirstStepToSecondStep)
                    }
                }.exhaustive
            }
        }
    }
}
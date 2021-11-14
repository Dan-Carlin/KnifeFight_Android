package com.flounderguy.knifefightutilities.ui.setup.finalstep

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.SetupFragmentFinalStepBinding
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SetupFinalStepFragment : Fragment(R.layout.setup_fragment_final_step) {

    private val finalStepViewModel: SetupFinalStepViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val finalStepBinding = SetupFragmentFinalStepBinding.bind(view)

        finalStepBinding.apply {
            buttonFinishSetup.setOnClickListener {
                finalStepViewModel.onSetupCompleted()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            finalStepViewModel.finalStepEvent.collect { event ->
                when (event) {
                    is SetupFinalStepViewModel.FinalStepEvent.NavigateToGameToolsScreen -> {
                        val actionFinalStepToGameTools =
                            SetupFinalStepFragmentDirections.actionSetupFinalStepFragmentToKnifeFightGameToolsFragment()
                        findNavController().navigate(actionFinalStepToGameTools)
                    }
                }.exhaustive
            }
        }
    }
}
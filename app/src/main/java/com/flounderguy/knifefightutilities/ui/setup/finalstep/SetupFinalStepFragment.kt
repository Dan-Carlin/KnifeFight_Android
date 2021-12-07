package com.flounderguy.knifefightutilities.ui.setup.finalstep

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.databinding.SetupFragmentFinalStepBinding
import com.flounderguy.knifefightutilities.ui.CharacterTraitAdapter
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SetupFinalStepFragment : Fragment(R.layout.setup_fragment_final_step),
    CharacterTraitAdapter.OnItemClickListener {

    private val finalStepViewModel: SetupFinalStepViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val finalStepBinding = SetupFragmentFinalStepBinding.bind(view)

        val traitAdapter = CharacterTraitAdapter(this)

        finalStepBinding.apply {
            textGangNameSetup.apply {
                text = finalStepViewModel.userGangName.value
                finalStepViewModel.userGangColor.value?.let {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            it.resValue
                        )
                    )
                }
            }

            recyclerViewFinalStep.apply {
                layoutManager = GridLayoutManager(requireContext(), 5)
                adapter = traitAdapter
            }

            buttonPreviousStepSetup.setOnClickListener {
                finalStepViewModel.onPreviousStepButtonClicked()
            }

            buttonFinishSetup.setOnClickListener {
                finalStepViewModel.onSetupCompleted()
            }
        }

        finalStepViewModel.traitList.observe(viewLifecycleOwner) {
            traitAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            finalStepViewModel.finalStepEvent.collect { event ->
                when (event) {
                    is SetupFinalStepViewModel.FinalStepEvent.NavigateBackToThirdStep -> {
                        findNavController().popBackStack()
                    }
                    is SetupFinalStepViewModel.FinalStepEvent.NavigateToGameToolsScreen -> {
                        val actionFinalStepToGameTools =
                            SetupFinalStepFragmentDirections.actionSetupFinalStepFragmentToGamePlayerToolsFragment()
                        findNavController().navigate(actionFinalStepToGameTools)
                    }
                }.exhaustive
            }
        }
    }

    override fun onCheckBoxClicked(trait: CharacterTrait, isChecked: Boolean) {
        finalStepViewModel.onTraitSelected(trait, isChecked)
    }

    override fun isUserTrait(trait: CharacterTrait): Boolean {
        return finalStepViewModel.isUserTrait(trait)
    }
}
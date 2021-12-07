package com.flounderguy.knifefightutilities.ui.setup.thirdstep

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
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.databinding.SetupFragmentSecondStepBinding
import com.flounderguy.knifefightutilities.databinding.SetupFragmentThirdStepBinding
import com.flounderguy.knifefightutilities.ui.CharacterTraitAdapter
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SetupThirdStepFragment : Fragment(R.layout.setup_fragment_third_step),
    CharacterTraitAdapter.OnItemClickListener {

    private val thirdStepViewModel: SetupThirdStepViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val thirdStepBinding = SetupFragmentThirdStepBinding.bind(view)

        val traitAdapter = CharacterTraitAdapter(this)

        thirdStepBinding.apply {
            textGangNameSetup.apply {
                text = thirdStepViewModel.gangName
                thirdStepViewModel.gangColor?.let {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            it.resValue
                        )
                    )
                }
            }

            recyclerViewThirdStep.apply {
                layoutManager = GridLayoutManager(requireContext(), 5)
                adapter = traitAdapter
            }

            buttonPreviousStepSetup.setOnClickListener {
                thirdStepViewModel.onPreviousStepButtonClicked()
            }

            buttonNextStepSetup.setOnClickListener {
                thirdStepViewModel.onThirdStepCompleted()
            }
        }

        thirdStepViewModel.traitIsSelected.observe(viewLifecycleOwner) {
            thirdStepBinding.buttonNextStepSetup.isEnabled = it
        }

        thirdStepViewModel.traitList.observe(viewLifecycleOwner) {
            traitAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            thirdStepViewModel.thirdStepEvent.collect { event ->
                when (event) {
                    is SetupThirdStepViewModel.ThirdStepEvent.NavigateBackToSecondStep -> {
                        findNavController().popBackStack()
                    }
                    is SetupThirdStepViewModel.ThirdStepEvent.NavigateToFinalStepScreen -> {
                        val actionThirdStepToFinalStep =
                            SetupThirdStepFragmentDirections.actionSetupThirdStepFragmentToSetupFinalStepFragment(
                                event.gang
                            )
                        findNavController().navigate(actionThirdStepToFinalStep)
                    }
                }.exhaustive
            }
        }
    }

    override fun onCheckBoxClicked(trait: CharacterTrait, isChecked: Boolean) {
        thirdStepViewModel.setUserTrait(trait)
    }

    override fun isUserTrait(trait: CharacterTrait): Boolean {
        return false
    }
}
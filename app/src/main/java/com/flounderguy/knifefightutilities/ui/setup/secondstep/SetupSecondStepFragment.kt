package com.flounderguy.knifefightutilities.ui.setup.secondstep

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.data.Gang
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
            textGangNameSetup.apply {
                text = secondStepViewModel.gangName
            }

            gangRedButton.setOnClickListener {
                setGangColor(Gang.Color.RED, secondStepBinding)
            }
            gangBlueButton.setOnClickListener {
                setGangColor(Gang.Color.BLUE, secondStepBinding)
            }
            gangGreenButton.setOnClickListener {
                setGangColor(Gang.Color.GREEN, secondStepBinding)
            }
            gangOrangeButton.setOnClickListener {
                setGangColor(Gang.Color.ORANGE, secondStepBinding)
            }
            gangPurpleButton.setOnClickListener {
                setGangColor(Gang.Color.PURPLE, secondStepBinding)
            }
            gangCyanButton.setOnClickListener {
                setGangColor(Gang.Color.CYAN, secondStepBinding)
            }
            gangWhiteButton.setOnClickListener {
                setGangColor(Gang.Color.WHITE, secondStepBinding)
            }
            gangBlackButton.setOnClickListener {
                setGangColor(Gang.Color.BLACK, secondStepBinding)
            }
            gangBrownButton.setOnClickListener {
                setGangColor(Gang.Color.BROWN, secondStepBinding)
            }
            gangYellowButton.setOnClickListener {
                setGangColor(Gang.Color.YELLOW, secondStepBinding)
            }
            gangPinkButton.setOnClickListener {
                setGangColor(Gang.Color.PINK, secondStepBinding)
            }
            gangDarkGreenButton.setOnClickListener {
                setGangColor(Gang.Color.DARK_GREEN, secondStepBinding)
            }

            buttonPreviousStepSetup.setOnClickListener {
                secondStepViewModel.onPreviousStepButtonClicked()
            }

            buttonNextStepSetup.apply {
                setOnClickListener {
                    secondStepViewModel.onSecondStepCompleted()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            secondStepViewModel.secondStepEvent.collect { event ->
                when (event) {
                    is SetupSecondStepViewModel.SecondStepEvent.NavigateBackToFirstStep -> {
                        findNavController().popBackStack()
                    }
                    is SetupSecondStepViewModel.SecondStepEvent.NavigateToThirdStepScreen -> {
                        val actionSecondStepToThirdStep =
                            SetupSecondStepFragmentDirections.actionSetupSecondStepFragmentToSetupThirdStepFragment(
                                event.name, event.color
                            )
                        findNavController().navigate(actionSecondStepToThirdStep)
                    }
                }.exhaustive
            }
        }
    }

    private fun setGangColor(color: Gang.Color, binding: SetupFragmentSecondStepBinding) {
        context?.let { binding.textGangNameSetup.setTextColor(ContextCompat.getColor(it, color.resValue)) }

        secondStepViewModel.gangColor = color

        binding.buttonNextStepSetup.isEnabled =
            secondStepViewModel.colorIsSelected.value == true
    }
}
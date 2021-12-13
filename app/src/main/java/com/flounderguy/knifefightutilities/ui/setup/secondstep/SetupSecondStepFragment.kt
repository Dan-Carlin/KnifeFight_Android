package com.flounderguy.knifefightutilities.ui.setup.secondstep

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.databinding.ItemGangColorButtonBinding
import com.flounderguy.knifefightutilities.databinding.ItemGangNameDisplayBinding
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

        val gangNameDisplay = layoutInflater.inflate(
            R.layout.item_gang_name_display,
            secondStepBinding.setupGangNameLayout,
            false
        )

        val gangNameBinding = ItemGangNameDisplayBinding.bind(gangNameDisplay)

        secondStepBinding.apply {
            setupGangNameLayout.addView(gangNameDisplay)

            buttonPreviousStepSetup.setOnClickListener {
                secondStepViewModel.onPreviousStepButtonClicked()
            }

            buttonNextStepSetup.apply {
                setOnClickListener {
                    secondStepViewModel.onSecondStepCompleted()
                }
            }
        }

        gangNameBinding.apply {
            textGangNameFill.apply {
                setTextColor(Color.GRAY)
                text = secondStepViewModel.gangName
            }

            textGangNameOutline.apply {
                text = secondStepViewModel.gangName
            }
        }

        secondStepViewModel.apply {
            colorIsSelected.observe(viewLifecycleOwner) {
                secondStepBinding.buttonNextStepSetup.isEnabled = it
            }

            colorArray.observe(viewLifecycleOwner) {
                var colorItemBinding: ItemGangColorButtonBinding

                for (i in it.indices) {

                    if (it[i] != Gang.Color.NONE) {

                        val gangColor = layoutInflater.inflate(
                            R.layout.item_gang_color_button,
                            secondStepBinding.colorGridLayout,
                            false
                        )
                        colorItemBinding = ItemGangColorButtonBinding.bind(gangColor)

                        val currentColor = it[i]

                        colorItemBinding.apply {
                            buttonGangColor.apply {
                                setBackgroundResource(currentColor.normalColorValue)
                                setOnClickListener {
                                    setGangColor(currentColor, gangNameBinding)
                                }
                            }
                        }

                        secondStepBinding.colorGridLayout.addView(gangColor, i)
                    }
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

    private fun setGangColor(
        color: Gang.Color,
        gangNameBinding: ItemGangNameDisplayBinding
    ) {
        context?.let {
            gangNameBinding.textGangNameOutline.setTextColor(
                ContextCompat.getColor(
                    it,
                    color.outerStrokeValue
                )
            )
        }

        context?.let {
            gangNameBinding.textGangNameOutline.setStroke(
                width = 24F,
                color = ContextCompat.getColor(it, color.outerStrokeValue),
                join = Paint.Join.MITER,
                miter = 0F
            )
        }

        context?.let {
            gangNameBinding.textGangNameFill.setTextColor(
                ContextCompat.getColor(
                    it,
                    color.normalColorValue
                )
            )
        }

        context?.let {
            gangNameBinding.textGangNameFill.setStroke(
                width = 6F,
                color = ContextCompat.getColor(it, color.innerStrokeValue),
                join = Paint.Join.MITER,
                miter = 0F
            )
        }

        secondStepViewModel.gangColor = color
    }
}
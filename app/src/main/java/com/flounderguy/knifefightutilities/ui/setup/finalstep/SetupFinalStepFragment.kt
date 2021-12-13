package com.flounderguy.knifefightutilities.ui.setup.finalstep

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.databinding.ItemCharacterTraitButtonBinding
import com.flounderguy.knifefightutilities.databinding.ItemGangNameDisplayBinding
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

        val gangNameDisplay = layoutInflater.inflate(
            R.layout.item_gang_name_display,
            finalStepBinding.setupGangNameLayout,
            false
        )

        val gangNameBinding = ItemGangNameDisplayBinding.bind(gangNameDisplay)

        setGangNameDisplay(gangNameBinding)

        finalStepBinding.apply {
            setupGangNameLayout.addView(gangNameDisplay)

            buttonPreviousStepSetup.setOnClickListener {
                finalStepViewModel.onPreviousStepButtonClicked()
            }

            buttonFinishSetup.setOnClickListener {
                finalStepViewModel.onSetupCompleted()
            }
        }

        finalStepViewModel.apply {
            rivalsAreSelected.observe(viewLifecycleOwner) {
                finalStepBinding.buttonFinishSetup.isEnabled = it
            }

            traitList.observe(viewLifecycleOwner) {
                var traitItemBinding: ItemCharacterTraitButtonBinding

                for (i in it.indices) {
                    val characterTrait = layoutInflater.inflate(
                        R.layout.item_character_trait_button,
                        finalStepBinding.characterTraitGroup,
                        false
                    )

                    traitItemBinding = ItemCharacterTraitButtonBinding.bind(characterTrait)

                    val currentTrait = it[i]

                    traitItemBinding.apply {
                        buttonUserTraitSelect.visibility = View.GONE
                        buttonRivalTraitSelect.visibility = View.VISIBLE

                        if (isUserTrait(currentTrait)) {
                            buttonDisabledOverlay.visibility = View.VISIBLE
                            buttonRivalTraitSelect.visibility = View.INVISIBLE
                        }

                        buttonRivalTraitSelect.apply {
                            setOnClickListener {
                                textVsLabel.isVisible = buttonRivalTraitSelect.isChecked
                                setRivalTrait(currentTrait)
                            }
                        }
                    }

                    populateTraitRoster(currentTrait, traitItemBinding)

                    finalStepBinding.characterTraitGroup.addView(characterTrait, i)
                }
            }
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

    private fun setGangNameDisplay(gangNameBinding: ItemGangNameDisplayBinding) {
        val gangName = finalStepViewModel.userGangName.value
        val gangColor = finalStepViewModel.userGangColor.value
        val gangTrait = finalStepViewModel.userGangTrait.value

        val gangDescription = "(The " + gangTrait?.name?.lowercase() + " ones)"
        val backgroundDrawable = gangTrait?.let { getTraitDrawable(it) }

        gangNameBinding.apply {
            textGangNameOutline.apply {
                text = gangName
                gangColor.let {
                    if (it != null) {
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                it.outerStrokeValue
                            )
                        )

                        setStroke(
                            width = 24F,
                            color = ContextCompat.getColor(context, it.outerStrokeValue),
                            join = Paint.Join.MITER,
                            miter = 0F
                        )
                    }
                }
            }

            textGangNameFill.apply {
                text = gangName
                gangColor.let {
                    if (it != null) {
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                it.normalColorValue
                            )
                        )

                        setStroke(
                            width = 6F,
                            color = ContextCompat.getColor(context, it.innerStrokeValue),
                            join = Paint.Join.MITER,
                            miter = 0F
                        )
                    }
                }
            }

            if (gangColor != null) {
                context?.let {
                    backgroundDrawable?.setTint(
                        ContextCompat.getColor(
                            it,
                            gangColor.darkColorValue
                        )
                    )
                }
            }

            gangNameBinding.apply {
                textGangDescription.apply {
                    visibility = View.VISIBLE
                    text = gangDescription
                }

                imageTraitSymbolBackground.apply {
                    visibility = View.VISIBLE
                    setImageDrawable(backgroundDrawable)
                }
            }
        }
    }

    private fun populateTraitRoster(
        trait: CharacterTrait,
        binding: ItemCharacterTraitButtonBinding
    ) {
        binding.apply {
            textTraitLabel.text = trait.name

            val iconDrawable = getTraitDrawable(trait)
            iconDrawable?.setTint(Color.DKGRAY)

            imageTraitThumb.setImageDrawable(iconDrawable)
        }
    }

    private fun setRivalTrait(trait: CharacterTrait) {
        finalStepViewModel.onTraitSelected(trait)
    }

    private fun getTraitDrawable(trait: Gang.Trait): Drawable? {
        val traitString = "ic_trait_symbol_" + trait.name.lowercase().replace('-', '_')
        val iconResourceId = resources.getIdentifier(
            traitString, "drawable",
            activity?.packageName
        )

        return context?.let { ContextCompat.getDrawable(it, iconResourceId) }
    }

    private fun getTraitDrawable(trait: CharacterTrait): Drawable? {
        val traitString = "ic_trait_symbol_" + trait.name.lowercase().replace('-', '_')
        val iconResourceId = resources.getIdentifier(
            traitString, "drawable",
            activity?.packageName
        )

        return context?.let { ContextCompat.getDrawable(it, iconResourceId) }
    }
}
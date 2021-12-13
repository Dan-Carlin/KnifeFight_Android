package com.flounderguy.knifefightutilities.ui.setup.thirdstep

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.databinding.ItemCharacterTraitButtonBinding
import com.flounderguy.knifefightutilities.databinding.ItemGangNameDisplayBinding
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

        val gangNameDisplay = layoutInflater.inflate(
            R.layout.item_gang_name_display,
            thirdStepBinding.setupGangNameLayout,
            false
        )

        val gangNameBinding = ItemGangNameDisplayBinding.bind(gangNameDisplay)

        setGangNameDisplay(gangNameBinding)

        thirdStepBinding.apply {
            setupGangNameLayout.addView(gangNameDisplay)

            buttonPreviousStepSetup.setOnClickListener {
                thirdStepViewModel.onPreviousStepButtonClicked()
            }

            buttonNextStepSetup.setOnClickListener {
                thirdStepViewModel.onThirdStepCompleted()
            }
        }

        thirdStepViewModel.apply {
            traitIsSelected.observe(viewLifecycleOwner) {
                thirdStepBinding.buttonNextStepSetup.isEnabled = it
            }

            traitList.observe(viewLifecycleOwner) {
                var traitItemBinding: ItemCharacterTraitButtonBinding

                for (i in it.indices) {
                    val characterTrait = layoutInflater.inflate(
                        R.layout.item_character_trait_button,
                        thirdStepBinding.characterTraitGroup,
                        false
                    )

                    traitItemBinding = ItemCharacterTraitButtonBinding.bind(characterTrait)

                    val currentTrait = it[i]

                    traitItemBinding.apply {
                        buttonUserTraitSelect.apply {
                            setOnClickListener {
                                setGangTrait(currentTrait, gangNameBinding)
                                val traitName = textTraitLabel.text as String
                                deselectAllOtherTraits(traitName, thirdStepBinding)
                            }
                        }
                    }

                    populateTraitRoster(currentTrait, traitItemBinding)

                    thirdStepBinding.characterTraitGroup.addView(characterTrait, i)
                }
            }
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

    private fun setGangNameDisplay(gangNameBinding: ItemGangNameDisplayBinding) {
        gangNameBinding.apply {
            textGangNameOutline.apply {
                text = thirdStepViewModel.gangName
                thirdStepViewModel.gangColor?.let {
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

            textGangNameFill.apply {
                text = thirdStepViewModel.gangName
                thirdStepViewModel.gangColor?.let {
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

    private fun setGangTrait(
        trait: CharacterTrait,
        gangNameBinding: ItemGangNameDisplayBinding
    ) {
        val gangDescription = "(The " + trait.name.lowercase() + " ones)"

        val backgroundDrawable = getTraitDrawable(trait)
        val userColor = thirdStepViewModel.gangColor
        if (userColor != null) {
            context?.let {
                backgroundDrawable?.setTint(
                    ContextCompat.getColor(
                        it,
                        userColor.darkColorValue
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

        thirdStepViewModel.setUserTrait(trait)
    }

    private fun getTraitDrawable(trait: CharacterTrait): Drawable? {
        val traitString = "ic_trait_symbol_" + trait.name.lowercase().replace('-', '_')
        val iconResourceId = resources.getIdentifier(
            traitString, "drawable",
            activity?.packageName
        )

        return context?.let { ContextCompat.getDrawable(it, iconResourceId) }
    }

    private fun deselectAllOtherTraits(
        traitName: String,
        thirdStepBinding: SetupFragmentThirdStepBinding
    ) {
        val traitGroup = thirdStepBinding.characterTraitGroup

        for (trait in traitGroup) {
            val traitItemBinding = ItemCharacterTraitButtonBinding.bind(trait)
            val currentName = traitItemBinding.textTraitLabel.text as String
            traitItemBinding.buttonUserTraitSelect.isChecked = traitName == currentName
        }
    }
}
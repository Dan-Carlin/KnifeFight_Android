package com.flounderguy.knifefightutilities.ui.setup

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.iterator
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.databinding.ItemCharacterTraitButtonBinding
import com.flounderguy.knifefightutilities.util.RadioGridGroup
import com.flounderguy.knifefightutilities.util.getTraitDrawable

/**
 * TraitRoster
 *
 * This class is in charge of creating a roster of trait buttons using the given list of
 * CharacterTrait objects.
 */
class TraitRoster(private val listener: OnItemClickListener) {

    fun createTraitRoster(
        context: Context,
        radioGroup: RadioGridGroup,
        traitList: List<CharacterTrait>,
        userTrait: Gang.Trait?
    ) {
        val inflater = LayoutInflater.from(context)

        for (i in traitList.indices) {
            val traitView = inflater.inflate(
                R.layout.item_character_trait_button,
                radioGroup,
                false
            )
            val traitItemBinding = ItemCharacterTraitButtonBinding.bind(traitView)

            val currentTrait = traitList[i]

            val iconDrawable = getTraitDrawable(context, traitItemBinding.imageTraitThumb, currentTrait)
            iconDrawable?.setTint(Color.DKGRAY)

            traitItemBinding.apply {
                textTraitLabel.text = currentTrait.name

                imageTraitThumb.setImageDrawable(iconDrawable)

                if (userTrait != null) {
                    buttonUserTraitSelect.visibility = View.GONE
                    buttonRivalTraitSelect.visibility = View.VISIBLE

                    if (listener.isUserTrait(currentTrait)) {
                        buttonDisabledOverlay.visibility = View.VISIBLE
                        buttonRivalTraitSelect.visibility = View.INVISIBLE
                    }
                }

                buttonUserTraitSelect.apply {
                    setOnClickListener {
                        val traitName = traitItemBinding.textTraitLabel.text as String
                        deselectAllOtherTraits(traitName, radioGroup)
                        listener.onTraitClick(traitView, currentTrait)
                    }
                }

                buttonRivalTraitSelect.apply {
                    setOnClickListener {
                        listener.onTraitClick(traitView, currentTrait)
                        textVsLabel.isVisible = buttonRivalTraitSelect.isChecked
                    }
                }
            }

            if (radioGroup.getChildAt(i) == null) {
                radioGroup.addView(traitView, i)
            }
        }
    }

    private fun deselectAllOtherTraits(
        traitName: String,
        traitGroup: RadioGridGroup
    ) {
        for (trait in traitGroup) {
            val traitItemBinding = ItemCharacterTraitButtonBinding.bind(trait)
            val currentName = traitItemBinding.textTraitLabel.text as String
            traitItemBinding.buttonUserTraitSelect.isChecked = traitName == currentName
        }
    }

    interface OnItemClickListener {
        fun onTraitClick(
            view: View,
            trait: CharacterTrait
        )

        fun isUserTrait(trait: CharacterTrait): Boolean
    }
}
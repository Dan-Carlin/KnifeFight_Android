package com.flounderguy.knifefightutilities.ui.setup

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.core.view.iterator
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.databinding.ItemCharacterTraitButtonBinding
import com.flounderguy.knifefightutilities.databinding.ItemGangButtonBinding
import com.flounderguy.knifefightutilities.util.RadioGridGroup
import com.flounderguy.knifefightutilities.util.getTraitDrawable

/**
 * CharacterRoster
 *
 * This class is in charge of creating a roster of trait or Gang buttons using the given list of
 * items.
 */
class CharacterRoster(private val listener: OnItemClickListener) {

    enum class RosterType {
        SELECT_USER,
        SELECT_RIVALS,
        RIVALS_REMAINING,
        RIVALS_ATTACK
    }

    fun createTraitRoster(
        context: Context,
        rosterType: RosterType,
        radioGroup: RadioGridGroup,
        traitList: List<CharacterTrait>
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

                if (rosterType == RosterType.SELECT_USER) {
                    buttonUserTraitSelect.visibility = View.VISIBLE
                    buttonRivalTraitSelect.visibility = View.GONE

                    if (listener.isUserTrait(currentTrait)) {
                        buttonUserTraitSelect.isChecked = true
                    }
                }

                if (rosterType == RosterType.SELECT_RIVALS) {
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

    fun createGangRoster(
        context: Context,
        rosterType: RosterType,
        radioGroup: RadioGridGroup,
        gangList: List<Gang>
    ) {
        val inflater = LayoutInflater.from(context)

        for (i in gangList.indices) {
            val gangView = inflater.inflate(
                R.layout.item_gang_button,
                radioGroup,
                false
            )
            val gangItemBinding = ItemGangButtonBinding.bind(gangView)

            val currentGang = gangList[i]

            val iconDrawable = currentGang.trait?.let {
                getTraitDrawable(context, gangItemBinding.imageTraitThumb,
                    it
                )
            }
            iconDrawable?.setTint(Color.WHITE)

            gangItemBinding.apply {

                imageTraitThumb.setImageDrawable(iconDrawable)

                if (rosterType == RosterType.RIVALS_REMAINING) {
                    buttonGangDefeated.visibility = View.VISIBLE
                }

                if (rosterType == RosterType.RIVALS_ATTACK) {

                }

                buttonGangDefeated.apply {
                    isChecked = currentGang.isDefeated

                    setOnClickListener {
                        currentGang.trait?.let { it1 ->
                            Log.d(
                                "CharRoster-DefeatBug",
                                it1.asString + " is checked: " + buttonGangDefeated.isChecked
                            ) }
                        listener.onGangClick(buttonGangDefeated, currentGang)
                    }
                }
            }

            if (radioGroup.getChildAt(i) == null) {
                radioGroup.addView(gangView, i)
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
        fun onGangClick(
            checkBox: CheckBox,
            gang: Gang
        )
        fun isUserTrait(trait: CharacterTrait): Boolean
    }
}
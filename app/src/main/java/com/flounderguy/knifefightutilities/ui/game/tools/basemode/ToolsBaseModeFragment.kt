package com.flounderguy.knifefightutilities.ui.game.tools.basemode

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.databinding.ToolsFragmentBaseModeBinding
import com.flounderguy.knifefightutilities.ui.setup.CharacterRoster
import com.flounderguy.knifefightutilities.ui.shared.GangDisplayFragment
import com.flounderguy.knifefightutilities.ui.shared.GangDisplayFragment.TraitDisplay
import com.flounderguy.knifefightutilities.util.getTraitDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ToolsBaseModeFragment : Fragment(R.layout.tools_fragment_base_mode),
    CharacterRoster.OnItemClickListener {

    private val baseModeViewModel: ToolsBaseModeViewModel by viewModels()

    // ViewBinding variable
    private lateinit var baseModeBinding: ToolsFragmentBaseModeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // GangDisplayFragment initialization
        val displayFragmentManager = parentFragmentManager
        val gangDisplayFragment = GangDisplayFragment.newInstance(TraitDisplay.HIDE)

        displayFragmentManager.beginTransaction().apply {
            replace(R.id.tools_gang_name_layout, gangDisplayFragment)
            commit()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewBinding variable
        baseModeBinding = ToolsFragmentBaseModeBinding.bind(view)

        // Instance of CharacterRoster class
        val gangRoster = CharacterRoster(this)

        baseModeViewModel.apply {
            userGang.observe(viewLifecycleOwner) {
                if (it.color != null && it.trait != null) {
                    setBaseModeDrawable(
                        gangTrait = it.trait,
                        gangColor = it.color
                    )
                }
            }


            rivalGangs.observe(viewLifecycleOwner) { rivals ->
                context?.let {
                    gangRoster.createGangRoster(
                        context = it,
                        rosterType = CharacterRoster.RosterType.RIVALS_REMAINING,
                        radioGroup = baseModeBinding.toolsRivalGangsGroup,
                        gangList = rivals
                    )
                }
            }
        }
    }

    private fun setBaseModeDrawable(gangTrait: Gang.Trait, gangColor: Gang.Color) {
        baseModeBinding = ToolsFragmentBaseModeBinding.bind(requireView())

        val backgroundDrawable = context?.let {
            getTraitDrawable(
                it,
                baseModeBinding.toolsBaseModeLayout,
                gangTrait
            )
        }!!

        // UI altering code
        gangColor.let {
            backgroundDrawable.setTint(ContextCompat.getColor(context!!, it.darkColorValue))
        }

        baseModeBinding.toolsBaseModeBackground.setImageDrawable(backgroundDrawable)
    }

    override fun onGangClick(checkBox: CheckBox, gang: Gang) {
        gang.trait?.let {
            Log.d(
                "BaseFrag-DefeatBug",
                it.asString + " is checked: " + checkBox.isChecked
            )
        }
        baseModeViewModel.onRivalSelect(gang, checkBox.isChecked)
    }

    override fun onTraitClick(view: View, trait: CharacterTrait) {
        return
    }

    override fun isUserTrait(trait: CharacterTrait): Boolean {
        return false
    }
}
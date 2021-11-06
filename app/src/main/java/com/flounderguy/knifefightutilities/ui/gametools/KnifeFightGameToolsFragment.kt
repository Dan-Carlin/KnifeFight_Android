package com.flounderguy.knifefightutilities.ui.gametools

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.KnifeFightFragmentGameToolsBinding
import com.flounderguy.knifefightutilities.ui.gametools.attackmode.GameToolsAttackModeDialogFragment
import com.flounderguy.knifefightutilities.ui.gametools.characterinfo.GameToolsCharacterInfoDialogFragment
import com.flounderguy.knifefightutilities.ui.gametools.editgangbanner.GameToolsEditGangBannerDialogFragment
import com.flounderguy.knifefightutilities.ui.gametools.howtouse.GameToolsHowToUseDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KnifeFightGameToolsFragment : Fragment(R.layout.knife_fight_fragment_game_tools) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val howToUseDialog = GameToolsHowToUseDialogFragment()
        howToUseDialog.show(parentFragmentManager, "How to Use Dialog")

        val gameToolsBinding = KnifeFightFragmentGameToolsBinding.bind(view)

        gameToolsBinding.apply {

            buttonAttackModeGameTools.setOnClickListener {
                val attackModeDialog = GameToolsAttackModeDialogFragment()
                attackModeDialog.show(parentFragmentManager, "Attack Mode Dialog")
            }

            buttonCharacterInfoGameTools.setOnClickListener {
                val characterInfoDialog = GameToolsCharacterInfoDialogFragment()
                characterInfoDialog.show(parentFragmentManager, "Character Info Dialog")
            }

            buttonEditGangBannerGameTools.setOnClickListener {
                val editGangBannerDialog = GameToolsEditGangBannerDialogFragment()
                editGangBannerDialog.show(parentFragmentManager, "Edit Gang Banner Dialog")
            }
        }
    }
}
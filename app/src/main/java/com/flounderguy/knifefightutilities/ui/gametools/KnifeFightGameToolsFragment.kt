package com.flounderguy.knifefightutilities.ui.gametools

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.KnifeFightFragmentGameToolsBinding
import com.flounderguy.knifefightutilities.ui.gametools.attackmode.GameToolsAttackModeDialogFragment
import com.flounderguy.knifefightutilities.ui.gametools.characterinfo.GameToolsCharacterInfoDialogFragment
import com.flounderguy.knifefightutilities.ui.gametools.editgangbanner.GameToolsEditGangBannerDialogFragment
import com.flounderguy.knifefightutilities.ui.gametools.howtouse.GameToolsHowToUseDialogFragment
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class KnifeFightGameToolsFragment : Fragment(R.layout.knife_fight_fragment_game_tools) {

    private val gameToolsViewModel: KnifeFightGameToolsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameToolsViewModel.onGameToolsStarted()

        val gameToolsBinding = KnifeFightFragmentGameToolsBinding.bind(view)

        gameToolsBinding.apply {
            buttonAttackModeGameTools.setOnClickListener {
                gameToolsViewModel.onAttackModeClicked()
            }
            buttonCharacterInfoGameTools.setOnClickListener {
                gameToolsViewModel.onCharacterInfoClicked()
            }
            buttonEditGangBannerGameTools.setOnClickListener {
                gameToolsViewModel.onEditGangBannerClicked()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            gameToolsViewModel.gameToolsEvent.collect { event ->
                when (event) {
                    is KnifeFightGameToolsViewModel.GameToolsEvent.ShowHowToUseDialog -> {
                        val howToUseDialog = GameToolsHowToUseDialogFragment()
                        howToUseDialog.show(parentFragmentManager, "How to Use Dialog")
                    }
                    is KnifeFightGameToolsViewModel.GameToolsEvent.ShowAttackModeDialog -> {
                        val attackModeDialog = GameToolsAttackModeDialogFragment()
                        attackModeDialog.show(parentFragmentManager, "Attack Mode Dialog")
                    }
                    is KnifeFightGameToolsViewModel.GameToolsEvent.ShowCharacterInfoDialog -> {
                        val characterInfoDialog = GameToolsCharacterInfoDialogFragment()
                        characterInfoDialog.show(parentFragmentManager, "Character Info Dialog")
                    }
                    is KnifeFightGameToolsViewModel.GameToolsEvent.ShowEditGangBannerDialog -> {
                        val editGangBannerDialog = GameToolsEditGangBannerDialogFragment()
                        editGangBannerDialog.show(parentFragmentManager, "Edit Gang Banner Dialog")
                    }
                    KnifeFightGameToolsViewModel.GameToolsEvent.NavigateToSettingsScreen -> {
                        val actionGameToolsToSettings =
                            KnifeFightGameToolsFragmentDirections.actionKnifeFightGameToolsFragmentToKnifeFightSettingsFragment()
                        findNavController().navigate(actionGameToolsToSettings)
                    }
                }.exhaustive
            }
        }
    }
}
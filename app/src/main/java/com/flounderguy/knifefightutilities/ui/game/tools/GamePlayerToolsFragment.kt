package com.flounderguy.knifefightutilities.ui.game.tools

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.GameFragmentPlayerToolsBinding
import com.flounderguy.knifefightutilities.ui.game.howtouse.GameHowToUseDialogFragment
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class GamePlayerToolsFragment : Fragment(R.layout.game_fragment_player_tools) {

    private val gameToolsViewModel: GamePlayerToolsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameToolsViewModel.onGameToolsStarted()

        val gameToolsBinding = GameFragmentPlayerToolsBinding.bind(view)

        gameToolsBinding.apply {
            buttonAttackModeTools.setOnClickListener {
                gameToolsViewModel.onAttackModeClicked()
            }
            buttonCounterattackModeTools.setOnClickListener {
                gameToolsViewModel.onCounterAttackClicked()
            }
            buttonCharacterInfoTools.setOnClickListener {
                gameToolsViewModel.onCharacterInfoClicked()
            }
            buttonEditGangTools.setOnClickListener {
                gameToolsViewModel.onEditGangClicked()
            }
            buttonSettingsTools.setOnClickListener {
                gameToolsViewModel.onSettingsClicked()
            }
            buttonHomeTools.setOnClickListener {
                gameToolsViewModel.onExitGameClicked()
            }
            buttonPlayerBannerTools.setOnClickListener {
                gameToolsViewModel.onBannerModeClicked()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            gameToolsViewModel.gameToolsEvent.collect { event ->
                when (event) {
                    is GamePlayerToolsViewModel.GameToolsEvent.ShowHowToUseDialog -> {
                        val howToUseDialog = GameHowToUseDialogFragment()
                        howToUseDialog.show(parentFragmentManager, "How to Use Dialog")
                    }
                    is GamePlayerToolsViewModel.GameToolsEvent.NavigateToAttackModeScreen -> {
                        val actionToolsToAttack =
                            GamePlayerToolsFragmentDirections.actionGamePlayerToolsFragmentToToolsAttackModeFragment()
                        findNavController().navigate(actionToolsToAttack)
                    }
                    is GamePlayerToolsViewModel.GameToolsEvent.NavigateToCounterAttackScreen -> {
                        val actionToolsToCounterattack =
                            GamePlayerToolsFragmentDirections.actionGamePlayerToolsFragmentToToolsCounterattackModeFragment()
                        findNavController().navigate(actionToolsToCounterattack)
                    }
                    is GamePlayerToolsViewModel.GameToolsEvent.NavigateToCharacterInfoScreen -> {
                        val actionToolsToCharacterInfo =
                            GamePlayerToolsFragmentDirections.actionGamePlayerToolsFragmentToToolsCharacterInfoFragment()
                        findNavController().navigate(actionToolsToCharacterInfo)
                    }
                    is GamePlayerToolsViewModel.GameToolsEvent.NavigateToEditGangBannerScreen -> {
                        val actionToolsToEditGang =
                            GamePlayerToolsFragmentDirections.actionGamePlayerToolsFragmentToToolsEditGangFragment()
                        findNavController().navigate(actionToolsToEditGang)
                    }
                    is GamePlayerToolsViewModel.GameToolsEvent.NavigateToSettingsScreen -> {
                        val actionToolsToSettings =
                            GamePlayerToolsFragmentDirections.actionGamePlayerToolsFragmentToKnifeFightSettingsFragment(
                                event.previousPage
                            )
                        findNavController().navigate(actionToolsToSettings)
                    }
                    is GamePlayerToolsViewModel.GameToolsEvent.NavigateToEndGameConfirmScreen -> {
                        // TODO: Create a fragment or dialog to navigate to.
                    }
                    is GamePlayerToolsViewModel.GameToolsEvent.NavigateToPlayerBannerScreen -> {
                        val actionToolsToBanner =
                            GamePlayerToolsFragmentDirections.actionGamePlayerToolsFragmentToGamePlayerBannerFragment(
                                event.hpCount
                            )
                    }
                    is GamePlayerToolsViewModel.GameToolsEvent.NavigateToSecondWindScreen -> {
                        // TODO: Create a fragment to navigate to.
                    }
                }.exhaustive
            }
        }
    }
}
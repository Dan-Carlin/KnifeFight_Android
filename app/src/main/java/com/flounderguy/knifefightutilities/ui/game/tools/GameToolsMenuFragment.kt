package com.flounderguy.knifefightutilities.ui.game.tools

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.GameFragmentToolsMenuBinding
import com.flounderguy.knifefightutilities.ui.game.howtouse.GameHowToUseDialogFragment
import com.flounderguy.knifefightutilities.ui.game.tools.attackmode.ToolsAttackModeFragment
import com.flounderguy.knifefightutilities.ui.game.tools.basemode.ToolsBaseModeFragment
import com.flounderguy.knifefightutilities.ui.game.tools.characterinfo.ToolsCharacterInfoFragment
import com.flounderguy.knifefightutilities.ui.game.tools.counterattackmode.ToolsCounterattackModeFragment
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class GameToolsMenuFragment : Fragment(R.layout.game_fragment_tools_menu) {

    private val gameToolsMenuViewModel: GameToolsMenuViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Forced landscape mode
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE


        val toolsBaseModeFragment = ToolsBaseModeFragment()

        parentFragmentManager.beginTransaction().apply {
            replace(R.id.game_tools_container_layout, toolsBaseModeFragment)
            commit()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolsAttackModeFragment = ToolsAttackModeFragment()
        val toolsCounterattackModeFragment = ToolsCounterattackModeFragment()
        val toolsCharacterInfoFragment = ToolsCharacterInfoFragment()

        if (gameToolsMenuViewModel.howToUseDialogHasShown.value == false) {
            gameToolsMenuViewModel.onGameToolsStarted()
        }

        val gameToolsBinding = GameFragmentToolsMenuBinding.bind(view)

        gameToolsBinding.apply {
            buttonAttackModeTools.setOnClickListener {
                gameToolsMenuViewModel.onAttackModeClicked()
            }
            buttonCounterattackModeTools.setOnClickListener {
                gameToolsMenuViewModel.onCounterAttackClicked()
            }
            buttonCharacterInfoTools.setOnClickListener {
                gameToolsMenuViewModel.onCharacterInfoClicked()
            }
            buttonSettingsTools.setOnClickListener {
                gameToolsMenuViewModel.onSettingsClicked()
            }
            buttonHomeTools.setOnClickListener {
                gameToolsMenuViewModel.onExitGameClicked()
            }
            buttonPlayerBannerTools.setOnClickListener {
                gameToolsMenuViewModel.onBannerModeClicked()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            gameToolsMenuViewModel.gameToolsMenuEvent.collect { event ->
                when (event) {
                    is GameToolsMenuViewModel.GameToolsEvent.ShowHowToUseDialog -> {
                        val howToUseDialog = GameHowToUseDialogFragment()
                        howToUseDialog.show(parentFragmentManager, "How to Use Dialog")
                    }
                    is GameToolsMenuViewModel.GameToolsEvent.NavigateToAttackModeScreen -> {
                        parentFragmentManager.beginTransaction().apply {
                            replace(R.id.game_tools_container_layout, toolsAttackModeFragment)
                            addToBackStack(null)
                            commit()
                        }
                    }
                    is GameToolsMenuViewModel.GameToolsEvent.NavigateToCounterAttackScreen -> {
                        parentFragmentManager.beginTransaction().apply {
                            replace(R.id.game_tools_container_layout, toolsCounterattackModeFragment)
                            addToBackStack(null)
                            commit()
                        }
                    }
                    is GameToolsMenuViewModel.GameToolsEvent.NavigateToCharacterInfoScreen -> {
                        parentFragmentManager.beginTransaction().apply {
                            replace(R.id.game_tools_container_layout, toolsCharacterInfoFragment)
                            addToBackStack(null)
                            commit()
                        }
                    }
                    is GameToolsMenuViewModel.GameToolsEvent.NavigateToSettingsScreen -> {
                        val actionToolsToSettings =
                            GameToolsMenuFragmentDirections.actionGameToolsMenuFragmentToKnifeFightSettingsFragment(
                                event.previousPage
                            )
                        findNavController().navigate(actionToolsToSettings)
                    }
                    is GameToolsMenuViewModel.GameToolsEvent.NavigateToEndGameConfirmScreen -> {
                        // TODO: Create a fragment or dialog to navigate to.
                    }
                    is GameToolsMenuViewModel.GameToolsEvent.NavigateToPlayerBannerScreen -> {
                        val actionToolsToBanner =
                            GameToolsMenuFragmentDirections.actionGameToolsMenuFragmentToGamePlayerBannerFragment()
                    }
                    is GameToolsMenuViewModel.GameToolsEvent.NavigateToSecondWindScreen -> {
                        // TODO: Create a fragment to navigate to.
                    }
                }.exhaustive
            }
        }
    }
}
package com.flounderguy.knifefightutilities.ui.setup.finalstep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.data.CharacterTrait
import com.flounderguy.knifefightutilities.databinding.SetupFragmentFinalStepBinding
import com.flounderguy.knifefightutilities.ui.setup.TraitRoster
import com.flounderguy.knifefightutilities.ui.shared.GangDisplayFragment
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * SetupFinalStepFragment is the fourth and final screen for setting up a new game.
 * This fragment is in charge of:
 *      - Taking input from the user for selecting all the rival gang traits.
 *      - Blocking navigation to tools fragment until at least one rival is chosen.
 */
@AndroidEntryPoint
class SetupFinalStepFragment : Fragment(R.layout.setup_fragment_final_step),
    TraitRoster.OnItemClickListener {

    /**
     * Variables
     */
    // This creates an instance of the viewModel for this fragment.
    private val finalStepViewModel: SetupFinalStepViewModel by viewModels()

    /**
     * Lifecycle methods
     */
    // This executes all the code that should run before returning the view.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // GangDisplayFragment initialization
        val displayFragmentManager = parentFragmentManager
        val gangDisplayFragment = GangDisplayFragment()
        val fragmentTransaction: FragmentTransaction = displayFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.setup_gang_name_layout, gangDisplayFragment)
            .addToBackStack(null)
        fragmentTransaction.commit()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    // This executes all the code that should run after creating the view.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewBinding variable
        val finalStepBinding = SetupFragmentFinalStepBinding.bind(view)

        // Instance of TraitRoster class
        val traitRoster = TraitRoster(this)

        // UI initialization and ViewModel interaction
        finalStepViewModel.apply {
            onFinalStepStarted()

            rivalsAreSelected.observe(viewLifecycleOwner) {
                finalStepBinding.buttonFinishSetup.isEnabled = it
            }

            traitList.observe(viewLifecycleOwner) { traits ->
                context?.let {
                    traitRoster.createTraitRoster(
                        context = it,
                        radioGroup = finalStepBinding.characterTraitGroup,
                        traitList = traits,
                        userTrait = finalStepViewModel.userTrait
                    )
                }
            }
        }

        // Button actions
        finalStepBinding.apply {
            buttonPreviousStepSetup.setOnClickListener {
                finalStepViewModel.onPreviousStepButtonClicked()
            }

            buttonFinishSetup.setOnClickListener {
                finalStepViewModel.onSetupCompleted()
            }
        }

        // Event channel implementation
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

    /**
     * Override methods
     */
    // Implementation of the methods in the TraitRoster click listener interface.
    override fun onTraitClick(view: View, trait: CharacterTrait) {
        finalStepViewModel.onTraitSelected(trait)
    }

    override fun isUserTrait(trait: CharacterTrait): Boolean {
        return finalStepViewModel.isUserTrait(trait)
    }
}
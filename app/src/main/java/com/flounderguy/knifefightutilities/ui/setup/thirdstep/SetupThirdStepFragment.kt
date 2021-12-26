package com.flounderguy.knifefightutilities.ui.setup.thirdstep

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
import com.flounderguy.knifefightutilities.databinding.SetupFragmentThirdStepBinding
import com.flounderguy.knifefightutilities.ui.setup.TraitRoster
import com.flounderguy.knifefightutilities.ui.shared.GangDisplayFragment
import com.flounderguy.knifefightutilities.util.convertTraitToLabel
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * SetupThirdStepFragment is the third screen for setting up a new game.
 * This fragment is in charge of:
 *      - Taking input from the user for selecting a gang trait.
 *      - Changing the state of the trait buttons to reflect the current stored value if a user
 *          navigates back to change it.
 *      - Blocking navigation to next step until a trait is chosen.
 */
@AndroidEntryPoint
class SetupThirdStepFragment : Fragment(R.layout.setup_fragment_third_step),
    TraitRoster.OnItemClickListener {

    /**
     * Variables
     */
    // This creates an instance of the viewModel for this fragment.
    private val thirdStepViewModel: SetupThirdStepViewModel by viewModels()

    // This creates the gang display sub fragment for this screen.
    private lateinit var gangDisplayFragment: GangDisplayFragment

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
        gangDisplayFragment = GangDisplayFragment()
        val displayFragmentManager = parentFragmentManager
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
        val thirdStepBinding = SetupFragmentThirdStepBinding.bind(view)

        // Instance of TraitRoster class
        val traitRoster = TraitRoster(this)

        // UI initialization and ViewModel interaction
        thirdStepViewModel.apply {
            traitIsSelected.observe(viewLifecycleOwner) {
                thirdStepBinding.buttonNextStepSetup.isEnabled = it
            }

            traitList.observe(viewLifecycleOwner) { traits ->
                context?.let {
                    traitRoster.createTraitRoster(
                        context = it,
                        radioGroup = thirdStepBinding.characterTraitGroup,
                        traitList = traits,
                        userTrait = null
                    )
                }
            }
        }

        // Button actions
        thirdStepBinding.apply {
            buttonPreviousStepSetup.setOnClickListener {
                thirdStepViewModel.onPreviousStepButtonClicked()
            }

            buttonNextStepSetup.setOnClickListener {
                thirdStepViewModel.onThirdStepCompleted()
            }
        }

        // Event channel implementation
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            thirdStepViewModel.thirdStepEvent.collect { event ->
                when (event) {
                    is SetupThirdStepViewModel.ThirdStepEvent.NavigateBackToSecondStep -> {
                        findNavController().popBackStack()
                    }
                    is SetupThirdStepViewModel.ThirdStepEvent.NavigateToFinalStepScreen -> {
                        val actionThirdStepToFinalStep =
                            SetupThirdStepFragmentDirections.actionSetupThirdStepFragmentToSetupFinalStepFragment()
                        findNavController().navigate(actionThirdStepToFinalStep)
                    }
                }.exhaustive
            }
        }
    }

    // This executes the code that should run every time the fragment is entered.
    override fun onStart() {
        super.onStart()
        thirdStepViewModel.onThirdStepStarted()
    }

    /**
     * Override methods
     */
    // Implementation of the methods in the TraitRoster click listener interface.
    override fun onTraitClick(view: View, trait: CharacterTrait) {
        thirdStepViewModel.setUserTrait(trait)

        val traitLabel = convertTraitToLabel(trait)
        gangDisplayFragment.setTraitDisplay(traitLabel, thirdStepViewModel.gangColor)
    }

    override fun isUserTrait(trait: CharacterTrait): Boolean {
        return false
    }
}
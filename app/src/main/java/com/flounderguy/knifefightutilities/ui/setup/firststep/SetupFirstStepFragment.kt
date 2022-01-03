package com.flounderguy.knifefightutilities.ui.setup.firststep

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.SetupFragmentFirstStepBinding
import com.flounderguy.knifefightutilities.ui.shared.GangDisplayFragment
import com.flounderguy.knifefightutilities.ui.shared.GangDisplayFragment.TraitDisplay
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * SetupFirstStepFragment is the first screen for setting up a new game.
 * This fragment is in charge of:
 *      - Taking input from the user for a gang name, or generating one if needed.
 *      - Populating the name field with the current name if a user navigates back to change it.
 *      - Blocking navigation to next step until the name field is no longer empty.
 */
@AndroidEntryPoint
class SetupFirstStepFragment : Fragment(R.layout.setup_fragment_first_step) {

    /**
     * Variables
     */
    // This creates an instance of the viewModel for this fragment.
    private val firstStepViewModel: SetupFirstStepViewModel by viewModels()

    // This creates the ViewBinding for this fragment.
    private lateinit var firstStepBinding: SetupFragmentFirstStepBinding

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
        val displayFragmentManager = parentFragmentManager
        gangDisplayFragment = GangDisplayFragment.newInstance(TraitDisplay.SHOW)

        displayFragmentManager.beginTransaction().apply {
            replace(R.id.setup_gang_name_layout, gangDisplayFragment)
            commit()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    // This executes all the code that should run after creating the view.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewBinding variable
        firstStepBinding = SetupFragmentFirstStepBinding.bind(view)

        // UI initialization and ViewModel interaction
        firstStepBinding.apply {

            // Button actions
            buttonHelpWithNameSetup.setOnClickListener {
                editGangNameSetup.setText(firstStepViewModel.onGenerateNameButtonPressed())
            }

            buttonCancelSetup.setOnClickListener {
                firstStepViewModel.onCancelButtonPressed()
            }

            buttonNextStepSetup.apply {
                setOnClickListener {
                    firstStepViewModel.onFirstStepCompleted()
                }
            }
        }

        // Event channel implementation
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            firstStepViewModel.firstStepEvent.collect { event ->
                when (event) {
                    is SetupFirstStepViewModel.FirstStepEvent.NavigateBackToHomeScreen -> {
                        findNavController().popBackStack()
                    }
                    is SetupFirstStepViewModel.FirstStepEvent.NavigateToSecondStepScreen -> {
                        val actionFirstStepToSecondStep =
                            SetupFirstStepFragmentDirections.actionSetupFirstStepFragmentToSetupSecondStepFragment(
                                event.color
                            )
                        findNavController().navigate(actionFirstStepToSecondStep)
                    }
                }.exhaustive
            }
        }
    }

    // This executes the code that should run every time the fragment is entered.
    override fun onStart() {
        super.onStart()
        firstStepBinding = SetupFragmentFirstStepBinding.bind(requireView())

        firstStepBinding.apply {
            buttonNextStepSetup.isEnabled = firstStepViewModel.gangName.isNotEmpty()

            editGangNameSetup.apply {
                setText(firstStepViewModel.gangName)

                addTextChangedListener {
                    firstStepViewModel.gangName = it.toString()
                    gangDisplayFragment.setNameDisplay(it.toString(), firstStepViewModel.gangColor)
                }

                addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        buttonNextStepSetup.isEnabled = s.toString().trim { it <= ' ' }.isNotEmpty()
                    }

                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                })
            }
        }
        firstStepViewModel.onFirstStepStarted()
    }
}

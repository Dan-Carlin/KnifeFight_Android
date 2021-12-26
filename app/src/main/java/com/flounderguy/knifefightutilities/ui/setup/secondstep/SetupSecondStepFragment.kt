package com.flounderguy.knifefightutilities.ui.setup.secondstep

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
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.databinding.ItemGangColorButtonBinding
import com.flounderguy.knifefightutilities.databinding.SetupFragmentSecondStepBinding
import com.flounderguy.knifefightutilities.ui.shared.GangDisplayFragment
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * SetupSecondStepFragment is the second screen for setting up a new game.
 * This fragment is in charge of:
 *      - Taking input from the user for selecting a gang color.
 *      - Changing the state of the color button to reflect the current stored value if a user
 *          navigates back to change it.
 *      - Blocking navigation to next step until a color is chosen.
 */
@AndroidEntryPoint
class SetupSecondStepFragment : Fragment(R.layout.setup_fragment_second_step) {

    /**
     * Variables
     */
    // This creates an instance of the viewModel for this fragment.
    private val secondStepViewModel: SetupSecondStepViewModel by viewModels()

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
        val secondStepBinding = SetupFragmentSecondStepBinding.bind(view)

        // UI initialization and ViewModel interaction
        secondStepViewModel.apply {
            colorIsSelected.observe(viewLifecycleOwner) {
                secondStepBinding.buttonNextStepSetup.isEnabled = it
            }

            colorArray.observe(viewLifecycleOwner) {
                var colorItemBinding: ItemGangColorButtonBinding

                for (i in it.indices) {

                    if (it[i] != Gang.Color.NONE) {

                        val gangColor = layoutInflater.inflate(
                            R.layout.item_gang_color_button,
                            secondStepBinding.colorGridLayout,
                            false
                        )
                        colorItemBinding = ItemGangColorButtonBinding.bind(gangColor)

                        val currentColor = it[i]

                        colorItemBinding.apply {
                            buttonGangColor.apply {
                                setBackgroundResource(currentColor.normalColorValue)
                                setOnClickListener {
                                    setGangColor(currentColor)
                                }
                            }
                        }

                        secondStepBinding.colorGridLayout.addView(gangColor, i)
                    }
                }
            }
        }

        // Button actions
        secondStepBinding.apply {
            buttonPreviousStepSetup.setOnClickListener {
                secondStepViewModel.onPreviousStepButtonClicked()
            }

            buttonNextStepSetup.apply {
                setOnClickListener {
                    secondStepViewModel.onSecondStepCompleted()
                }
            }
        }

        // Event channel implementation
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            secondStepViewModel.secondStepEvent.collect { event ->
                when (event) {
                    is SetupSecondStepViewModel.SecondStepEvent.NavigateBackToFirstStep -> {
                        findNavController().popBackStack()
                    }
                    is SetupSecondStepViewModel.SecondStepEvent.NavigateToThirdStepScreen -> {
                        val actionSecondStepToThirdStep =
                            SetupSecondStepFragmentDirections.actionSetupSecondStepFragmentToSetupThirdStepFragment()
                        findNavController().navigate(actionSecondStepToThirdStep)
                    }
                }.exhaustive
            }
        }
    }

    // This executes the code that should run every time the fragment is entered.
    override fun onStart() {
        super.onStart()
        secondStepViewModel.onSecondStepStarted()
    }

    /**
     * Setter method
     */
    // This method sets the value of the viewModel color variable and the display color.
    private fun setGangColor(color: Gang.Color) {
        secondStepViewModel.gangColor = color
        gangDisplayFragment.setColorDisplay(color)
    }
}
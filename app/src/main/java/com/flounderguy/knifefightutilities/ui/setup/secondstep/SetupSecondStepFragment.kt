package com.flounderguy.knifefightutilities.ui.setup.secondstep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.databinding.ItemGangColorButtonBinding
import com.flounderguy.knifefightutilities.databinding.SetupFragmentSecondStepBinding
import com.flounderguy.knifefightutilities.ui.shared.GangDisplayFragment
import com.flounderguy.knifefightutilities.ui.shared.GangDisplayFragment.TraitDisplay
import com.flounderguy.knifefightutilities.util.RadioGridGroup
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
    private val gangDisplayFragment = GangDisplayFragment.newInstance(TraitDisplay.SHOW)

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
        val secondStepBinding = SetupFragmentSecondStepBinding.bind(view)

        // UI initialization and ViewModel interaction
        secondStepViewModel.apply {
            colorIsSelected.observe(viewLifecycleOwner) {
                secondStepBinding.buttonNextStepSetup.isEnabled = it
            }

            colorArray.observe(viewLifecycleOwner) {
                createColorGrid(it, secondStepBinding.colorGridLayout)
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
                            SetupSecondStepFragmentDirections.actionSetupSecondStepFragmentToSetupThirdStepFragment(
                                event.trait
                            )
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
     * Private methods
     */
    // This method populates the empty GridLayout with a set of color buttons.
    private fun createColorGrid(
        colors: Array<Gang.Color>,
        radioGrid: RadioGridGroup
    ) {
        var colorItemBinding: ItemGangColorButtonBinding

        for (i in colors.indices) {

            val currentColor = colors[i]

            if (currentColor != Gang.Color.NONE) {

                val gangColor = layoutInflater.inflate(
                    R.layout.item_gang_color_button,
                    radioGrid,
                    false
                )
                colorItemBinding = ItemGangColorButtonBinding.bind(gangColor)

                val colorDrawable =
                    context?.let { ContextCompat.getDrawable(it, R.drawable.color_button_oval) }
                colorDrawable?.setTint(context?.let {
                    ContextCompat.getColor(
                        it,
                        currentColor.normalColorValue
                    )
                }!!)

                colorItemBinding.apply {
                    if (secondStepViewModel.isUserColor(currentColor)) {
                        buttonGangColor.isChecked = true
                        buttonColorCheck.isVisible = buttonGangColor.isChecked
                    }

                    buttonColorCheck.drawable.setTint(context?.let {
                        ContextCompat.getColor(
                            it,
                            currentColor.darkColorValue
                        )
                    }!!)

                    buttonGangColor.apply {
                        background = colorDrawable
                        tag = currentColor.name

                        setOnClickListener {
                            setGangColor(currentColor)
                            deselectAllOtherColors(currentColor, radioGrid)
                        }
                    }
                }
                radioGrid.addView(gangColor, i)
            }
        }
    }

    // This method makes sure only the button for the chosen color is checked.
    private fun deselectAllOtherColors(
        color: Gang.Color,
        colorGrid: RadioGridGroup
    ) {
        for (colorButton in colorGrid) {
            val colorItemBinding = ItemGangColorButtonBinding.bind(colorButton)
            val currentColorTag = colorItemBinding.buttonGangColor.tag as String
            colorItemBinding.buttonGangColor.isChecked = color.name == currentColorTag
            colorItemBinding.buttonColorCheck.isVisible = colorItemBinding.buttonGangColor.isChecked
        }
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
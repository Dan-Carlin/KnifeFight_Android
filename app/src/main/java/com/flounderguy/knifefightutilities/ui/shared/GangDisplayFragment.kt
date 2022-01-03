package com.flounderguy.knifefightutilities.ui.shared

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.data.Gang
import com.flounderguy.knifefightutilities.databinding.FragmentGangNameDisplayBinding
import com.flounderguy.knifefightutilities.util.getTraitDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

/**
 * GangDisplayFragment is the fragment that displays the stylized info for the Gang object.
 * This fragment is in charge of:
 *      - Providing methods to customize its UI from other classes and fragments.
 *      - Providing enough flexibility to be useful in any fragment that needs a Gang display.
 */
@AndroidEntryPoint
class GangDisplayFragment : Fragment(R.layout.fragment_gang_name_display) {

    enum class TraitDisplay {
        SHOW,
        HIDE
    }

    /**
     * Variables
     */
    // This creates an instance of the viewModel for this fragment.
    private val nameDisplayViewModel: GangDisplayViewModel by viewModels()

    // ViewBinding variable. Since it's needed in every method, it is declared here.
    private lateinit var nameDisplayBinding: FragmentGangNameDisplayBinding

    // This executes all the code that should run after creating the view.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val traitArg = arguments?.getSerializable("trait_mode")

        // ViewBinding variable
        nameDisplayBinding = FragmentGangNameDisplayBinding.bind(view)

        // UI initialization and ViewModel interaction
        nameDisplayBinding.apply {
            val emptyDrawable = context?.let {
                getTraitDrawable(
                    it,
                    imageTraitSymbolBackground,
                    Gang.Trait.NONE
                )
            }!!
            imageTraitSymbolBackground.setImageDrawable(emptyDrawable)
        }

        setNameDisplay("", Gang.Color.NONE)

        nameDisplayViewModel.userGangExists.observe(viewLifecycleOwner) {
            if (it == true) {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                    nameDisplayViewModel.userGangFlow.collect { gang ->
                        if (gang.name.isNotEmpty() && gang.color != null) {
                            setNameDisplay(gang.name, gang.color)
                        }

                        if (gang.color != Gang.Color.NONE && gang.color != null) {
                            setColorDisplay(gang.color)
                        }

                        if (traitArg == TraitDisplay.SHOW) {
                            if (gang.trait != Gang.Trait.NONE) {
                                if (gang.color != null && gang.trait != null)
                                    setTraitDisplay(gang.trait, gang.color)
                            }
                        }
                    }
                }
            }
        }

        if (traitArg == TraitDisplay.HIDE) {
            hideTraitDisplay()
        }
    }

    /**
     * Setter methods
     */
    // These methods give other classes the flexibility to alter the Gang display UI.
    fun setNameDisplay(gangName: String, gangColor: Gang.Color?) {
        // ViewBinding variable
        nameDisplayBinding = FragmentGangNameDisplayBinding.bind(requireView())

        // UI altering code
        nameDisplayBinding.apply {
            textGangNameOutline.apply {
                visibility = View.VISIBLE
                text = gangName
            }
            textGangNameFill.apply {
                visibility = View.VISIBLE
                text = gangName
                if (gangColor != Gang.Color.NONE && gangColor != null) {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            gangColor.normalColorValue
                        )
                    )
                } else {
                    setTextColor(Color.GRAY)
                }
            }
        }
    }

    fun setColorDisplay(gangColor: Gang.Color) {
        // ViewBinding variable
        nameDisplayBinding = FragmentGangNameDisplayBinding.bind(requireView())

        // UI altering code
        nameDisplayBinding.apply {
            textGangNameOutline.apply {
                gangColor.let {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            it.outerStrokeValue
                        )
                    )

                    setStroke(
                        width = 24F,
                        color = ContextCompat.getColor(context, it.outerStrokeValue),
                        join = Paint.Join.MITER,
                        miter = 0F
                    )
                }
            }

            textGangNameFill.apply {
                gangColor.let {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            it.normalColorValue
                        )
                    )

                    setStroke(
                        width = 6F,
                        color = ContextCompat.getColor(context, it.innerStrokeValue),
                        join = Paint.Join.MITER,
                        miter = 0F
                    )
                }
            }

            gangColor.let {
                imageTraitSymbolBackground.drawable.setTint(
                    ContextCompat.getColor(
                        context!!,
                        it.darkColorValue
                    )
                )
            }
        }
    }

    fun setTraitDisplay(gangTrait: Gang.Trait, gangColor: Gang.Color) {
        // ViewBinding variable
        nameDisplayBinding = FragmentGangNameDisplayBinding.bind(requireView())

        // Drawable variables
        val gangDescription = "(The " + gangTrait.asString.lowercase() + " ones)"
        val backgroundDrawable = context?.let {
            getTraitDrawable(
                it,
                nameDisplayBinding.imageTraitSymbolBackground,
                gangTrait
            )
        }!!

        // UI altering code
        gangColor.let {
            backgroundDrawable.setTint(ContextCompat.getColor(context!!, it.darkColorValue))
        }

        nameDisplayBinding.apply {
            textGangDescription.apply {
                if (gangTrait != Gang.Trait.NONE) {
                    visibility = View.VISIBLE
                    text = gangDescription
                }
            }

            imageTraitSymbolBackground.apply {
                if (gangTrait != Gang.Trait.NONE) {
                    visibility = View.VISIBLE
                }
                setImageDrawable(backgroundDrawable)
            }
        }
    }

    private fun hideTraitDisplay() {
        // ViewBinding variable
        nameDisplayBinding = FragmentGangNameDisplayBinding.bind(requireView())

        // UI altering code
        nameDisplayBinding.apply {
            textGangDescription.visibility = View.GONE
            imageTraitSymbolBackground.visibility = View.GONE
        }
    }

    companion object {
        fun newInstance(traitMode: TraitDisplay): GangDisplayFragment {
            val displayArg = Bundle()
            displayArg.putSerializable("trait_mode", traitMode)

            val newGangDisplayFragment = GangDisplayFragment()
            newGangDisplayFragment.arguments = displayArg

            return newGangDisplayFragment
        }
    }
}
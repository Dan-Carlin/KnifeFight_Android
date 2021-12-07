package com.flounderguy.knifefightutilities.ui.setup.firststep

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.SetupFragmentFirstStepBinding
import com.flounderguy.knifefightutilities.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SetupFirstStepFragment : Fragment(R.layout.setup_fragment_first_step) {

    private val firstStepViewModel: SetupFirstStepViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstStepBinding = SetupFragmentFirstStepBinding.bind(view)

        firstStepBinding.apply {
            editGangNameSetup.apply {
                setText(firstStepViewModel.gangName)

                addTextChangedListener {
                    firstStepViewModel.gangName = it.toString()
                }

                addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        buttonNextStepSetup.isEnabled = s.toString().trim { it <= ' ' }.isNotEmpty()
                    }

                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    }

                    override fun afterTextChanged(s: Editable) {
                    }
                })
            }

            buttonHelpWithNameSetup.setOnClickListener {
                editGangNameSetup.setText(firstStepViewModel.generateGangName())
            }

            buttonCancelSetup.setOnClickListener {
                firstStepViewModel.onCancelButtonPressed()
            }

            buttonNextStepSetup.apply {
                isEnabled = editGangNameSetup.text.isNotEmpty()

                setOnClickListener {
                    firstStepViewModel.onFirstStepCompleted()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            firstStepViewModel.firstStepEvent.collect { event ->
                when (event) {
                    is SetupFirstStepViewModel.FirstStepEvent.NavigateBackToHomeScreen -> {
                        findNavController().popBackStack()
                    }
                    is SetupFirstStepViewModel.FirstStepEvent.NavigateToSecondStepScreen -> {
                        val actionFirstStepToSecondStep =
                            SetupFirstStepFragmentDirections.actionSetupFirstStepFragmentToSetupSecondStepFragment(
                                event.name
                            )
                        findNavController().navigate(actionFirstStepToSecondStep)
                    }
                }.exhaustive
            }
        }
    }
}
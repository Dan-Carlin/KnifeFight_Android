package com.flounderguy.knifefightutilities.ui.setup.secondstep

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.SetupFragmentSecondStepBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupSecondStepFragment : Fragment(R.layout.setup_fragment_second_step) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val secondStepBinding = SetupFragmentSecondStepBinding.bind(view)

        secondStepBinding.apply {

            buttonStepThreeSetup.setOnClickListener {
                val actionSecondStepToThirdStep =
                    SetupSecondStepFragmentDirections.actionSetupSecondStepFragmentToSetupThirdStepFragment()
                findNavController().navigate(actionSecondStepToThirdStep)
            }
        }
    }
}
package com.flounderguy.knifefightutilities.ui.setup.firststep

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.SetupFragmentFirstStepBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFirstStepFragment : Fragment(R.layout.setup_fragment_first_step) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstStepBinding = SetupFragmentFirstStepBinding.bind(view)

        firstStepBinding.apply {

            buttonStepTwoSetup.setOnClickListener {
                val actionFirstStepToSecondStep =
                    SetupFirstStepFragmentDirections.actionSetupFirstStepFragmentToSetupSecondStepFragment()
                findNavController().navigate(actionFirstStepToSecondStep)
            }
        }
    }
}
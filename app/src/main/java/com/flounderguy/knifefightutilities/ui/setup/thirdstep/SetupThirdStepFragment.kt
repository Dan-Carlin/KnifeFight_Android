package com.flounderguy.knifefightutilities.ui.setup.thirdstep

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.SetupFragmentThirdStepBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupThirdStepFragment : Fragment(R.layout.setup_fragment_third_step) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val thirdStepBinding = SetupFragmentThirdStepBinding.bind(view)

        thirdStepBinding.apply {

            buttonFinishSetup.setOnClickListener {
                val actionThirdStepToGameTools =
                    SetupThirdStepFragmentDirections.actionSetupThirdStepFragmentToKnifeFightGameToolsFragment()
                findNavController().navigate(actionThirdStepToGameTools)
            }
        }
    }
}
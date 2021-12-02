package com.flounderguy.knifefightutilities.ui.game.tools.rollresults

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.ToolsFragmentRollResultsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ToolsRollResultsFragment : Fragment(R.layout.tools_fragment_roll_results) {

    private val rollResultsViewModel: ToolsRollResultsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rollResultsBinding = ToolsFragmentRollResultsBinding.bind(view)

        rollResultsBinding.apply {
            buttonCloseRollResults.setOnClickListener {
                rollResultsViewModel.onConfirmClicked()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            rollResultsViewModel.rollResultsEvent.collect { event ->
                when (event) {
                    is ToolsRollResultsViewModel.RollResultsEvent.NavigateBackToPreviousScreen -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
}
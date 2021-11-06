package com.flounderguy.knifefightutilities.ui.gametools.howtouse

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.GameToolsDialogFragmentHowToUseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameToolsHowToUseDialogFragment :
    DialogFragment(R.layout.game_tools_dialog_fragment_how_to_use) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val howToUseBinding = GameToolsDialogFragmentHowToUseBinding.bind(view)

        howToUseBinding.apply {

            buttonCloseHowToUse.setOnClickListener {
                dismiss()
            }
        }
    }

}
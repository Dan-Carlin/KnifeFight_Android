package com.flounderguy.knifefightutilities.ui.game.howtouse

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.GameDialogFragmentHowToUseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameHowToUseDialogFragment :
    DialogFragment(R.layout.game_dialog_fragment_how_to_use) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val howToUseBinding = GameDialogFragmentHowToUseBinding.bind(view)

        howToUseBinding.apply {

            buttonCloseHowToUse.setOnClickListener {
                dismiss()
            }
        }
    }
}
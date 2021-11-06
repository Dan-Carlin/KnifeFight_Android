package com.flounderguy.knifefightutilities.ui.gametools.attackmode

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.GameToolsDialogFragmentAttackModeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameToolsAttackModeDialogFragment :
    DialogFragment(R.layout.game_tools_dialog_fragment_attack_mode) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val attackModeBinding = GameToolsDialogFragmentAttackModeBinding.bind(view)

        attackModeBinding.apply {

            buttonCloseAttackMode.setOnClickListener {
                dismiss()
            }
        }
    }
}
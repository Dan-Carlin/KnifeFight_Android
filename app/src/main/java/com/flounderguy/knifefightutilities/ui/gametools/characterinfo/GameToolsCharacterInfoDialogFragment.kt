package com.flounderguy.knifefightutilities.ui.gametools.characterinfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.GameToolsDialogFragmentCharacterInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameToolsCharacterInfoDialogFragment :
    DialogFragment(R.layout.game_tools_dialog_fragment_character_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val characterInfoBinding = GameToolsDialogFragmentCharacterInfoBinding.bind(view)

        characterInfoBinding.apply {

            buttonCloseCharacterInfo.setOnClickListener {
                dismiss()
            }
        }
    }
}
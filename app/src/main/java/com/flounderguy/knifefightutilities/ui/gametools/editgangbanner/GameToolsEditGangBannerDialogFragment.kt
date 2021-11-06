package com.flounderguy.knifefightutilities.ui.gametools.editgangbanner

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.GameToolsDialogFragmentEditGangBannerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameToolsEditGangBannerDialogFragment :
    DialogFragment(R.layout.game_tools_dialog_fragment_edit_gang_banner) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editGangBannerBinding = GameToolsDialogFragmentEditGangBannerBinding.bind(view)

        editGangBannerBinding.apply {

            buttonCloseEditGangBanner.setOnClickListener {
                dismiss()
            }
        }
    }
}
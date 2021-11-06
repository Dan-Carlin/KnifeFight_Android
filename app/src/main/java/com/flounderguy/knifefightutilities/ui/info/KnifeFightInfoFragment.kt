package com.flounderguy.knifefightutilities.ui.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import com.flounderguy.knifefightutilities.databinding.KnifeFightFragmentInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KnifeFightInfoFragment : Fragment(R.layout.knife_fight_fragment_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val infoBinding = KnifeFightFragmentInfoBinding.bind(view)

        infoBinding.apply {

            buttonAboutInfo.setOnClickListener {
                val actionInfoToAbout =
                    KnifeFightInfoFragmentDirections.actionKnifeFightInfoFragmentToInfoAboutFragment()
                findNavController().navigate(actionInfoToAbout)
            }

            buttonRulesInfo.setOnClickListener {
                val actionInfoToRules =
                    KnifeFightInfoFragmentDirections.actionKnifeFightInfoFragmentToInfoRulesFragment()
                findNavController().navigate(actionInfoToRules)
            }

            buttonCardListInfo.setOnClickListener {
                val actionInfoToCardList =
                    KnifeFightInfoFragmentDirections.actionKnifeFightInfoFragmentToInfoCardListFragment()
                findNavController().navigate(actionInfoToCardList)
            }
        }
    }
}
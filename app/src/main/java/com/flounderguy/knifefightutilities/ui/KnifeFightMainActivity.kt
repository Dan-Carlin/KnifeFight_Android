package com.flounderguy.knifefightutilities.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.flounderguy.knifefightutilities.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KnifeFightMainActivity : AppCompatActivity() {

    private lateinit var knifeFightNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.knife_fight_activity_main)

        val knifeFightNavHostFragment =
            supportFragmentManager.findFragmentById(R.id.knife_fight_nav_host_fragment) as NavHostFragment
        knifeFightNavController = knifeFightNavHostFragment.findNavController()
    }
}
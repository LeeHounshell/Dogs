package com.harlie.dogs.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val _tag = "LEE: <" + MainActivity::class.java.simpleName + ">"

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag(_tag).d("onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // setup a back button
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController, null)
    }

    override fun onSupportNavigateUp(): Boolean {
        Timber.tag(_tag).d("onSupportNavigateUp")
        return NavigationUI.navigateUp(navController, null)
    }

}

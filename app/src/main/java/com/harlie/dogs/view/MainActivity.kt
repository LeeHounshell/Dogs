package com.harlie.dogs.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = "LEE: <" + MainActivity::class.java.simpleName + ">"

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.tag(TAG).d("onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // setup a back button
        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, null)
    }

    override fun onSupportNavigateUp(): Boolean {
        Timber.tag(TAG).d("onSupportNavigateUp")
        return NavigationUI.navigateUp(navController, null)
    }

}

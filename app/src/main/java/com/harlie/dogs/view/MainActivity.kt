package com.harlie.dogs.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.R
import com.harlie.dogs.util.NavigationErrorEvent
import com.harlie.dogs.util.PERMISSION_SEND_SMS
import com.harlie.dogs.util.RoomErrorEvent
import com.harlie.dogs.util.RxErrorEvent
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
        try {
            setupActionBarWithNavController(navController, null)
        }
        catch (e: IllegalStateException) {
            Timber.tag(_tag).e("PROBLEM SETTING THE ACTION BAR! e=${e}")
        }
    }

    fun checkSmsPermission() {
        Timber.tag(_tag).d("checkSmsPermission")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                // show permission rational
                AlertDialog.Builder(this)
                    .setTitle("Send SMS Permission")
                    .setMessage("The Dogs app requires permission to send a SMS")
                    .setPositiveButton("Ask me") {dialog, which ->
                        requestSmsPermission()
                    }
                    .setNegativeButton("No") {dialog, which ->
                        notifyDetailFragment(false)
                    }
                    .show()
            }
            else {
                requestSmsPermission()
            }
        }
        else {
            notifyDetailFragment(true)
        }
    }

    private fun requestSmsPermission() {
        Timber.tag(_tag).d("requestSmsPermission")
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Timber.tag(_tag).d("onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    notifyDetailFragment(true)
                }
                else {
                    notifyDetailFragment(false)
                }
            }
        }
    }

    private fun getFragment(): Fragment? {
        Timber.tag(_tag).d("getFragment")
        val fragmentManager = supportFragmentManager
        val navHostFragment = fragmentManager.primaryNavigationFragment
        return navHostFragment?.childFragmentManager?.primaryNavigationFragment
    }

    private fun notifyDetailFragment(permissionGranted: Boolean) {
        Timber.tag(_tag).d("notifyDetailFragment permissionGranted=${permissionGranted}")
        getFragment().let {
            if (it is DetailFragment) {
                it.onPermissionResult(permissionGranted)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        Timber.tag(_tag).d("onSupportNavigateUp")
        return NavigationUI.navigateUp(navController, null)
    }

    override fun onStart() {
        Timber.tag(_tag).d("onStart")
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        Timber.tag(_tag).d("onStop")
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNavigationErrorEvent(navigationError_event: NavigationErrorEvent) {
        Timber.tag(_tag).e("onNavigationErrorEvent: ${navigationError_event}")
        Toast.makeText(this, navigationError_event.description, Toast.LENGTH_LONG).show()
        recover()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRoomErrorEvent(roomError_event: RoomErrorEvent) {
        Timber.tag(_tag).e("onRoomErrorEvent: ${roomError_event}")
        Toast.makeText(this, roomError_event.description, Toast.LENGTH_LONG).show()
        recover()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRxErrorEvent(rxError_event: RxErrorEvent) {
        Timber.tag(_tag).e("onRxErrorEvent: ${rxError_event}")
        Toast.makeText(this, rxError_event.description, Toast.LENGTH_LONG).show()
        recover()
    }

    private fun recover() {
        Timber.tag(_tag).e("recover")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}

package com.harlie.dogs

import androidx.multidex.MultiDexApplication
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.util.ReleaseTree

class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree());
        }
        else {
            //FIXME: Fabric.with(this, Crashlytics())
            Timber.plant(ReleaseTree())
        }
    }

}

package com.harlie.dogs

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.util.ReleaseTree

class MyApplication : MultiDexApplication() {

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    fun getInstance(): MyApplication? {
        return instance
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        else {
            //FIXME: Fabric.with(this, Crashlytics())
            Timber.plant(ReleaseTree())
        }
    }

}

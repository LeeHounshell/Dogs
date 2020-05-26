package com.harlie.dogs.util

import android.util.Log
import timber.log.Timber

class ReleaseTree : Timber.Tree() {

    override fun isLoggable(priority: Int): Boolean {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return false
        }
        return true
        // only log WARN(Timber.w), ERROR(Timber.e), or WTF(Timber.wtf)
    }

    @Suppress("DEPRECATION")
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        // Don't log VERBOSE, DEBUG and INFO
        if (! isLoggable(priority)) {
            return
        }

        if (priority == Log.ERROR){
            val t = throwable ?: Exception(message)
            if (tag == null) {
                Timber.tag("LEE: ")
            }
            Log.println(priority, "Test->$tag", t.toString())

            // for Crashlytics
            //FIXME: Crashlytics.setInt(CRASHLYTICS_KEY_PRIORITY, priority)
            //FIXME: Crashlytics.setString(CRASHLYTICS_KEY_TAG, tag)
            //FIXME: Crashlytics.setString(CRASHLYTICS_KEY_MESSAGE, message)
            //FIXME: Crashlytics.logException(t)
        }

    }

    companion object {
        private const val CRASHLYTICS_KEY_PRIORITY = "Priority"
        private const val CRASHLYTICS_KEY_TAG = "Tag"
        private const val CRASHLYTICS_KEY_MESSAGE = "Message"
    }
}
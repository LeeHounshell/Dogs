package com.harlie.dogs.util

import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.harlie.dogs.MyApplication

class SharedPreferencesHelper {

    val context = MyApplication.applicationContext()
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)!!

    companion object {
        private const val PREF_HAVE_DATABASE = "pref_have_database"
        private const val PREF_TIME = "pref_time"
        private const val PREF_DURATION = "pref_cache_duration"

        @Volatile private var instance: SharedPreferencesHelper? = null
        private val LOCK = Any()

        operator fun invoke(): SharedPreferencesHelper = instance ?: synchronized(LOCK) {
            instance ?: buildHelper().also {
                instance = it
            }
        }

        private fun buildHelper() : SharedPreferencesHelper {
            return SharedPreferencesHelper()
        }
    }

    fun saveUpdateTime(time: Long) {
        prefs.edit(commit = true) {putLong(PREF_TIME, time)}
    }

    fun getUpdateTime() = prefs.getLong(PREF_TIME, 0)

    fun getCacheDuration() = prefs.getString(PREF_DURATION, "600")

    fun isDatabaseCreated() = prefs.getBoolean(PREF_HAVE_DATABASE, false)

    fun markDatabaseCreated() {
        prefs.edit(commit = true) {putBoolean(PREF_HAVE_DATABASE, true)}
    }

}

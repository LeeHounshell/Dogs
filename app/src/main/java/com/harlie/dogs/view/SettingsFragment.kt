package com.harlie.dogs.view

import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.R

class SettingsFragment : PreferenceFragmentCompat() {
    private val _tag = "LEE: <" + SettingsFragment::class.java.simpleName + ">"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Timber.tag(_tag).d("onCreatePreferences")
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onBindPreferences() {
        Timber.tag(_tag).d("onBindPreferences")
        findPreference<EditTextPreference>("pref_cache_duration")?.setOnBindEditTextListener {
            it.setSingleLine()
        }
    }

}
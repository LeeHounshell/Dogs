package com.harlie.dogs.repository

import androidx.lifecycle.ViewModel
import com.github.ajalt.timberkt.Timber

open class MyViewModel: ViewModel() {
    private val TAG = "LEE: <" + MyViewModel::class.java.simpleName + ">"

    init {
        Timber.tag(TAG).d("init")
    }

}

package com.harlie.dogs.viewmodel

import androidx.lifecycle.ViewModel
import com.github.ajalt.timberkt.Timber

open class MyViewModel: ViewModel() {
    private val tag = "LEE: <" + MyViewModel::class.java.simpleName + ">"

    init {
        Timber.tag(tag).d("init")
    }

}

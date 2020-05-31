package com.harlie.dogs.viewmodel

import androidx.lifecycle.ViewModel
import com.github.ajalt.timberkt.Timber

open class MyViewModel: ViewModel() {
    private val _tag = "LEE: <" + MyViewModel::class.java.simpleName + ">"

    init {
        Timber.tag(_tag).d("init")
    }

}

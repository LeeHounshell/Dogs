package com.harlie.dogs.repository

import com.github.ajalt.timberkt.Timber

open class DataRepository {
    private val TAG = "LEE: <" + DataRepository::class.java.simpleName + ">"

    init {
        Timber.tag(TAG).d("init")
    }

}

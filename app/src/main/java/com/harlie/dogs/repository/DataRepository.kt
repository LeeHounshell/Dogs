package com.harlie.dogs.repository

import com.github.ajalt.timberkt.Timber

open class DataRepository {
    private val _tag = "LEE: <" + DataRepository::class.java.simpleName + ">"

    init {
        Timber.tag(_tag).d("init")
    }

}

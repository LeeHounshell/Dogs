package com.harlie.dogs.repository

import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

open class DataRepository {
    private val _tag = "LEE: <" + DataRepository::class.java.simpleName + ">"

    init {
        Timber.tag(_tag).d("init")
    }

    val job = Job()
    val databaseScope = CoroutineScope(job + Dispatchers.Main)
}

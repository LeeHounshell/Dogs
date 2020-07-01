package com.harlie.dogs.viewmodel

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModel
import com.github.ajalt.timberkt.Timber

open class MyViewModel: ViewModel(), LifecycleOwner {
    private val _tag = "LEE: <" + MyViewModel::class.java.simpleName + ">"

    private lateinit var lifecycleRegistry: LifecycleRegistry

    init {
        Timber.tag(_tag).d("init")
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    override fun getLifecycle(): Lifecycle {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        return lifecycleRegistry
    }

    override fun onCleared() {
        Timber.tag(_tag).d("onCleared")
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

}

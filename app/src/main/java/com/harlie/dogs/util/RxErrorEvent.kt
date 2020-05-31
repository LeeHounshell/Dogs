package com.harlie.dogs.util

import com.github.ajalt.timberkt.Timber
import org.greenrobot.eventbus.EventBus

class RxErrorEvent(description: String) {
    private val _tag = "LEE: <" + RxErrorEvent::class.java.simpleName + ">"

    var errorDescription: String = description

    fun post() {
        Timber.tag(_tag).d("post $errorDescription")
        EventBus.getDefault().post(this)
    }

}


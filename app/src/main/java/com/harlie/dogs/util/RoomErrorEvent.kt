package com.harlie.dogs.util

import com.github.ajalt.timberkt.Timber
import org.greenrobot.eventbus.EventBus

class RoomErrorEvent(description: String) {
    private val _tag = "LEE: <" + RoomErrorEvent::class.java.simpleName + ">"

    var errorDescription: String = description

    fun post() {
        Timber.tag(_tag).d("post $errorDescription")
        EventBus.getDefault().post(this)
    }

    override fun toString(): String {
        return "RoomErrorEvent(errorDescription='$errorDescription')"
    }

}
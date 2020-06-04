package com.harlie.dogs.util

import com.github.ajalt.timberkt.Timber
import org.greenrobot.eventbus.EventBus

open class ErrorEvent(open val description: String) {
    private val _tag = "LEE: <" + ErrorEvent::class.java.simpleName + ">"

    var errorDescription: String = description

    fun post() {
        Timber.tag(_tag).d("post $errorDescription")
        EventBus.getDefault().post(this)
    }

    override fun toString(): String {
        return "ErrorEvent(errorDescription='$errorDescription')"
    }

}

class RoomErrorEvent(override val description: String): ErrorEvent(description)
class RxErrorEvent(override val description: String): ErrorEvent(description)
class NavigationErrorEvent(override val description: String): ErrorEvent(description)


package com.harlie.dogs.util

import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.model.DogBreed
import org.greenrobot.eventbus.EventBus

open class EventBusEvent(open val description: String) {
    private val _tag = "LEE: <" + EventBusEvent::class.java.simpleName + ">"

    fun post() {
        Timber.tag(_tag).d("post $description")
        EventBus.getDefault().post(this)
    }

    override fun toString(): String {
        return "EventBusEvent(errorDescription='$description')"
    }

}

class RoomLoadedEvent(override val description: String, val dogsList: List<DogBreed>): EventBusEvent(description)
class RoomErrorEvent(override val description: String): EventBusEvent(description)
class RxErrorEvent(override val description: String): EventBusEvent(description)
class NavigationErrorEvent(override val description: String): EventBusEvent(description)


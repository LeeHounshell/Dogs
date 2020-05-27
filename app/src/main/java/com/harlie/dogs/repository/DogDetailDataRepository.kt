package com.harlie.dogs.repository

import com.github.ajalt.timberkt.Timber

class DogDetailDataRepository(dogId: Int): DataRepository() {
    private val _tag = "LEE: <" + DogDetailDataRepository::class.java.simpleName + ">"

    private val dogUuid: Int

    init {
        Timber.tag(_tag).d("init $dogId")
        dogUuid = dogId
    }

}

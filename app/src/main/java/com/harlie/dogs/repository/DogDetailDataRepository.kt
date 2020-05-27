package com.harlie.dogs.repository

import com.github.ajalt.timberkt.Timber

class DogDetailDataRepository(dogId: Int): DataRepository() {
    private val TAG = "LEE: <" + DogDetailDataRepository::class.java.simpleName + ">"

    private val dogUuid: Int

    init {
        Timber.tag(TAG).d("init $dogId")
        dogUuid = dogId
    }

}

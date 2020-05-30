package com.harlie.dogs.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.MyApplication
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.room.DogDatabase
import com.harlie.dogs.util.default

class DogDetailDataRepository(dogId: Int): DataRepository() {
    private val _tag = "LEE: <" + DogDetailDataRepository::class.java.simpleName + ">"

    private val dogUuid: Int

    init {
        Timber.tag(_tag).d("init $dogId")
        dogUuid = dogId
    }

    suspend fun fetchFromDatabase(): LiveData<DogBreed> {
        Timber.tag(_tag).d("fetchFromDatabase dogUuid=${dogUuid}")
        val dog = DogDatabase(MyApplication.applicationContext()).dogDao().getDog(dogUuid)
        return MutableLiveData<DogBreed>().default(dog)
    }

}

package com.harlie.dogs.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.room.DogDatabase

class DogDetailDataRepository(dogId: Int): DataRepository() {
    private val _tag = "LEE: <" + DogDetailDataRepository::class.java.simpleName + ">"

    private val dogUuid: Int = dogId

    init {
        Timber.tag(_tag).d("init $dogId")
    }

    suspend fun fetchFromDatabase(context: Context): LiveData<DogBreed> {
        Timber.tag(_tag).d("fetchFromDatabase dogUuid=${dogUuid}")
        val dogDao = DogDatabase.getInstance(context)!!.dogDao()
        val dog = dogDao.getDog(dogUuid)
        val mutableLiveData: MutableLiveData<DogBreed> = MutableLiveData()
        mutableLiveData.value = dog
        return mutableLiveData
    }

}

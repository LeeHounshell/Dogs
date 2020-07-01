package com.harlie.dogs.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.model.DogsApiService
import com.harlie.dogs.room.DogDatabase
import com.harlie.dogs.util.SharedPreferencesHelper

class DogDetailDataRepository(private val dogId: Int, apiService: DogsApiService, prefHelper: SharedPreferencesHelper): DataRepository() {
    private val _tag = "LEE: <" + DogDetailDataRepository::class.java.simpleName + ">"

    init {
        Timber.tag(_tag).d("init $dogId")
    }

    suspend fun fetchFromDatabase(context: Context): LiveData<DogBreed> {
        Timber.tag(_tag).d("fetchFromDatabase dogId=${dogId}")
        val dogDao = DogDatabase.getInstance(context)!!.dogDao()
        val dog = dogDao.getDog(dogId)
        val mutableLiveData: MutableLiveData<DogBreed> = MutableLiveData()
        mutableLiveData.value = dog
        return mutableLiveData
    }

}

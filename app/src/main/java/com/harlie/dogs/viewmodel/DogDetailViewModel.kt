package com.harlie.dogs.viewmodel

import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.repository.DogDetailDataRepository
import com.harlie.dogs.repository.MyViewModel

class DogDetailViewModel(val repository: DogDetailDataRepository): MyViewModel() {
    private val TAG = "LEE: <" + DogDetailViewModel::class.java.simpleName + ">"

    private val dogRepository: DogDetailDataRepository
    val dogLiveDetail = MutableLiveData<DogBreed>()

    init {
        Timber.tag(TAG).d("init")
        dogRepository = repository
    }

    fun fetch() {
        Timber.tag(TAG).d("refresh")
        // FIXME: dummy data
        val dog = DogBreed("3", "Siberian Husky", "20 years", "group", "purpose", "temperament", "")

        dogLiveDetail.value = dog
    }

}

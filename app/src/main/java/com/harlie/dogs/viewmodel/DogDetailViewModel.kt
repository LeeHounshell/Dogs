package com.harlie.dogs.viewmodel

import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.repository.DogDetailDataRepository

class DogDetailViewModel(private val repository: DogDetailDataRepository): MyViewModel() {
    private val tag = "LEE: <" + DogDetailViewModel::class.java.simpleName + ">"

    private val dogRepository: DogDetailDataRepository
    val dogLiveDetail = MutableLiveData<DogBreed>()

    init {
        Timber.tag(tag).d("init")
        dogRepository = repository
    }

    fun fetch() {
        Timber.tag(tag).d("refresh")
        // FIXME: dummy data
        val dog = DogBreed("3", "Siberian Husky", "20 years", "group", "purpose", "temperament", "")

        dogLiveDetail.value = dog
    }

}

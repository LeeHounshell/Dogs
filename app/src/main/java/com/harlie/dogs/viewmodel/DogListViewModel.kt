package com.harlie.dogs.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.harlie.dogs.model.DogBreed

class DogListViewModel: ViewModel() {
    val dogsLiveList = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val dogsLoading =  MutableLiveData<Boolean>()

    fun refresh() {
        // FIXME: dummy data
        val dog1 = DogBreed("1", "Corgi", "15 years", "group", "purpose", "temperament", "")
        val dog2 = DogBreed("2", "Labrador", "10 years", "group", "purpose", "temperament", "")
        val dog3 = DogBreed("3", "Siberian Husky", "20 years", "group", "purpose", "temperament", "")
        val dogsList = arrayListOf<DogBreed>(dog1, dog2, dog3)

        dogsLiveList.value = dogsList
        dogsLoadError.value = false
        dogsLoading.value = false
    }
}
package com.harlie.dogs.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.repository.DogDetailDataRepository
import kotlinx.coroutines.launch

class DogDetailViewModel(private val repository: DogDetailDataRepository): MyViewModel() {
    private val tag = "LEE: <" + DogDetailViewModel::class.java.simpleName + ">"

    private val dogRepository: DogDetailDataRepository

    private val dogMutableDetail by lazy {
        MutableLiveData<DogBreed>()
    }

    init {
        Timber.tag(tag).d("init")
        dogRepository = repository
    }

    // Fragment Observers subscribe to this immutable LiveData
    val dog: LiveData<DogBreed>
        get() = dogMutableDetail

    suspend fun fetch() {
        Timber.tag(tag).d("fetch")
        viewModelScope.launch {
            fetchDogFromDatabase().observeForever { dog ->
                Timber.tag(tag).d("db observeForever dog=${dog}")
                dogMutableDetail.postValue(dog)
            }
        }
    }

    // Initiate a Database API call to get the DogBreed
    private suspend fun fetchDogFromDatabase(): LiveData<DogBreed> {
        Timber.tag(tag).d("fetchDogFromDatabase")
        return dogRepository.fetchFromDatabase()
    }

}

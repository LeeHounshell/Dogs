package com.harlie.dogs.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.repository.DogsListDataRepository
import kotlinx.coroutines.launch

class DogsListViewModel(private val repository: DogsListDataRepository): MyViewModel() {
    private val tag = "LEE: <" + DogsListViewModel::class.java.simpleName + ">"

    private val dogsRepository: DogsListDataRepository

    private val dogsMutableList by lazy {
        MutableLiveData<List<DogBreed>>()
    }
    private val dogsMutableLoading by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        Timber.tag(tag).d("init")
        dogsMutableLoading.value = true
        dogsRepository = repository
    }

    // Fragment Observers subscribe to these immutable LiveData
    val dogsList: LiveData<List<DogBreed>>
        get() = dogsMutableList

    val dogsLoading: LiveData<Boolean>
        get() = dogsMutableLoading

    suspend fun refresh() {
        Timber.tag(tag).d("refresh")
        viewModelScope.launch {
            fetchDogsFromRemote().observeForever { dogsList ->
                Timber.tag(tag).d("observeForever dogsList.size=${dogsList.size}")
                dogsMutableList.postValue(dogsList)
            }
        }
    }

    // Initiate a Network API call to get the list of DogBreed
    private suspend fun fetchDogsFromRemote(): LiveData<List<DogBreed>> {
        Timber.tag(tag).d("fetchDogsFromRemote")
        return dogsRepository.fetchFromRemote()
    }

    fun loadingComplete() {
        Timber.tag(tag).d("loadingComplete")
        dogsMutableLoading.value = false
    }

    override fun onCleared() {
        Timber.tag(tag).d("onCleared")
        super.onCleared()
    }

}
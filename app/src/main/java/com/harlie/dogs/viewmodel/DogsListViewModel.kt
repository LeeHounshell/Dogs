package com.harlie.dogs.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.MyApplication
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.repository.DogsListDataRepository
import com.harlie.dogs.util.isNetworkAvailable
import kotlinx.coroutines.launch


class DogsListViewModel(private val repository: DogsListDataRepository): MyViewModel() {
    private val tag = "LEE: <" + DogsListViewModel::class.java.simpleName + ">"

    private val dogsRepository: DogsListDataRepository
    private val refreshTime = 5 * 60 * 1000 * 1000 * 1000L // 5 minutes in nanoseconds

    private val dogsMutableList by lazy {
        MutableLiveData<List<DogBreed>>()
    }
    private val dogsMutableLoading by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        Timber.tag(tag).d("init")
        dogsMutableLoading.value = false
        dogsRepository = repository
    }

    // Fragment Observers subscribe to these immutable LiveData
    val dogsList: LiveData<List<DogBreed>>
        get() = dogsMutableList

    val dogsLoading: LiveData<Boolean>
        get() = dogsMutableLoading

    suspend fun refresh() {
        Timber.tag(tag).d("refresh")
        dogsMutableLoading.postValue(true)
        val updateTime = dogsRepository.prefHelper.getUpdateTime()
        Timber.tag(tag).d("refresh updateTime=${updateTime}")
        if (! isNetworkAvailable(MyApplication.applicationContext()) || ((updateTime != null) && (updateTime != 0L) && ((System.nanoTime() - updateTime) < refreshTime))) {
            Timber.tag(tag).d("refresh: fetch data from database")
            viewModelScope.launch {
                fetchDogsFromDatabase().observeForever { dogsList ->
                    Timber.tag(tag).d("db observeForever dogsList.size=${dogsList.size}")
                    dogsMutableList.postValue(dogsList)
                }
            }
        }
        else {
            Timber.tag(tag).d("refresh: fetch data from network")
            viewModelScope.launch {
                fetchDogsFromRemote().observeForever { dogsList ->
                    Timber.tag(tag).d("net observeForever dogsList.size=${dogsList.size}")
                    dogsMutableList.postValue(dogsList)
                    dogsRepository.storeDogsLocally(dogsList)
                }
            }
        }
    }

    // Initiate a Database API call to get the list of DogBreed
    private suspend fun fetchDogsFromDatabase(): LiveData<List<DogBreed>> {
        Timber.tag(tag).d("fetchDogsFromDatabase")
        return dogsRepository.fetchFromDatabase()
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
        dogsRepository.onCleared()
    }

}
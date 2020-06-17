package com.harlie.dogs.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.MyApplication
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.repository.DogsListDataRepository
import com.harlie.dogs.util.SharedPreferencesHelper
import com.harlie.dogs.util.isNetworkAvailable
import kotlinx.coroutines.launch


class DogsListViewModel(repository: DogsListDataRepository): MyViewModel() {
    private val _tag = "LEE: <" + DogsListViewModel::class.java.simpleName + ">"

    private val dogsRepository: DogsListDataRepository
    private val prefHelper = SharedPreferencesHelper(MyApplication.applicationContext())
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L // 5 minutes in nanoseconds

    private val dogsMutableList by lazy {
        MutableLiveData<List<DogBreed>>()
    }
    private val dogsMutableLoading by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        Timber.tag(_tag).d("init")
        dogsRepository = repository
        viewModelScope.launch {
            dogsMutableLoading.postValue(false)
            initializeData(MyApplication.applicationContext())
        }
    }

    // Fragment Observers subscribe to these immutable LiveData
    val dogsList: LiveData<List<DogBreed>>
        get() = dogsMutableList

    val dogsLoading: LiveData<Boolean>
        get() = dogsMutableLoading

    suspend fun initializeData(context: Context) {
        Timber.tag(_tag).d("initializeData")
        if ( !prefHelper.isDatabaseCreated()) {
            Timber.tag(_tag).d("initializeData: create initial database")
            viewModelScope.launch {
                fetchDogsFromDatabase(context).observeForever { dogsList ->
                    Timber.tag(_tag).d("initializeData: observeForever dogsList.size=${dogsList.size}")
                    if (dogsList.isNotEmpty()) {
                        dogsRepository.storeDogsLocally(dogsList)
                        dogsMutableList.postValue(dogsList)
                    }
                }
            }
            Timber.tag(_tag).d("initializeData: mark database created")
            prefHelper.markDatabaseCreated()
        }
    }

    suspend fun refresh() {
        Timber.tag(_tag).d("refresh")
        dogsMutableLoading.postValue(true)
        checkCacheDuration()
        val updateTime = prefHelper.getUpdateTime()
        Timber.tag(_tag).d("refresh: updateTime=${updateTime}")
        if ( ! isNetworkAvailable(MyApplication.applicationContext()) || ((updateTime != null) && (updateTime != 0L) && ((System.nanoTime() - updateTime) < refreshTime))) {
            Timber.tag(_tag).d("refresh: fetch data from database")
            viewModelScope.launch {
                fetchDogsFromDatabase(MyApplication.applicationContext()).observeForever { dogsList ->
                    Timber.tag(_tag).d("refresh: db observeForever dogsList.size=${dogsList.size}")
                    dogsMutableList.postValue(dogsList)
                }
            }
        }
        else {
            Timber.tag(_tag).d("refresh: fetch data from network")
            viewModelScope.launch {
                fetchDogsFromRemote().observeForever { dogsList ->
                    Timber.tag(_tag).d("refresh: net observeForever dogsList.size=${dogsList.size}")
                    dogsRepository.storeDogsLocally(dogsList)
                    dogsMutableList.postValue(dogsList)
                }
            }
        }
    }

    private fun checkCacheDuration() {
        Timber.tag(_tag).d("checkCacheDuration")
        val cachePreference = prefHelper.getCacheDuration()
        try {
            val cachePreferenceInt = cachePreference?.toInt() ?: 5 * 60
            refreshTime = cachePreferenceInt.times(1000 * 1000 * 1000L) // seconds to nano seconds
            Timber.tag(_tag).d("checkCacheDuration: new refreshTime=${refreshTime}")
        }
        catch (e: NumberFormatException) {
            Timber.tag(_tag).e("checkCacheDuration: bad number e=${e}")
        }
    }

    // Initiate a Database API call to get the list of DogBreed
    private suspend fun fetchDogsFromDatabase(context: Context): LiveData<List<DogBreed>> {
        Timber.tag(_tag).d("fetchDogsFromDatabase")
        return dogsRepository.fetchFromDatabase(context)
    }

    // Initiate a Network API call to get the list of DogBreed
    private suspend fun fetchDogsFromRemote(): LiveData<List<DogBreed>> {
        Timber.tag(_tag).d("fetchDogsFromRemote")
        return dogsRepository.fetchFromRemote()
    }

    fun loadingComplete() {
        Timber.tag(_tag).d("loadingComplete")
        dogsMutableLoading.value = false
    }

    override fun onCleared() {
        Timber.tag(_tag).d("onCleared")
        super.onCleared()
        dogsRepository.onCleared()
    }

}
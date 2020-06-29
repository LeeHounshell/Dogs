package com.harlie.dogs.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.MyApplication
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.repository.DogsListDataRepository
import com.harlie.dogs.room.DogDatabase
import com.harlie.dogs.util.isNetworkAvailable
import kotlinx.coroutines.launch

class DogsListViewModel(repository: DogsListDataRepository): MyViewModel() {
    private val _tag = "LEE: <" + DogsListViewModel::class.java.simpleName + ">"

    private val dogsRepository: DogsListDataRepository
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L // 5 minutes in nanoseconds

    @Volatile
    private var didNetworkRefresh = false

    private val dogsMutableList by lazy {
        MutableLiveData<List<DogBreed>>()
    }
    private val dogsMutableLoading by lazy {
        MutableLiveData<Boolean>()
    }

    var lastClickedDogListIndex = 0

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
        if ( !dogsRepository.prefHelper.isDatabaseCreated()) {
            Timber.tag(_tag).d("initializeData: create initial database")
            viewModelScope.launch {
                fetchDogsFromDatabase(context).observeForever { dogsList ->
                    Timber.tag(_tag).d("initializeData: observeForever dogsList.size=${dogsList.size}")
                    if (dogsList.isNotEmpty()) {
                        synchronized (DogDatabase.Companion.GLOBAL_ACCESS_LOCK) {
                            dogsRepository.storeDogsLocally(dogsList)
                            dogsMutableList.postValue(dogsList)
                        }
                    }
                }
            }
            Timber.tag(_tag).d("initializeData: mark database created")
            dogsRepository.prefHelper.markDatabaseCreated()
        }
    }

    fun checkRefresh() {
        Timber.tag(_tag).d("checkRefresh")
        if (dogsList.value == null || dogsList.value?.size == 0) {
            Timber.tag(_tag).d("checkRefresh: do full refresh")
            viewModelScope.launch {
                refresh()
            }
        }
        else {
            Timber.tag(_tag).d("checkRefresh: show existing")
            dogsMutableList.postValue(dogsList.value)
        }
    }

    suspend fun refresh() {
        Timber.tag(_tag).d("--------- refresh")
        dogsMutableLoading.postValue(true)
        checkCacheDuration()
        val context = MyApplication.applicationContext()
        val updateTime = dogsRepository.prefHelper.getUpdateTime()
        Timber.tag(_tag).d("refresh: updateTime=${updateTime}")
        if (((updateTime != 0L) && ((System.nanoTime() - updateTime) < refreshTime))
            || ! isNetworkAvailable())
        {
            Timber.tag(_tag).d("refresh: fetch data from DATABASE")
            databaseRefresh(context)
        }
        else if (isNetworkAvailable()){
            Timber.tag(_tag).d("refresh: fetch data from NETWORK")
            networkRefresh()
        }
    }

    private fun databaseRefresh(context: Context) {
        Timber.tag(_tag).d("databaseRefresh")
        viewModelScope.launch {
            fetchDogsFromDatabase(context).observeForever { dogsList ->
                Timber.tag(_tag).d("databaseRefresh: observeForever dogsList.size=${dogsList.size}")
                dogsMutableList.postValue(dogsList)
            }
        }
    }

    private fun networkRefresh() {
        Timber.tag(_tag).d("networkRefresh")
        viewModelScope.launch {
            fetchDogsFromRemote().observeForever { dogsList ->
                Timber.tag(_tag).d("networkRefresh: observeForever dogsList.size=${dogsList.size}")
                synchronized(DogDatabase.GLOBAL_ACCESS_LOCK) {
                    didNetworkRefresh = true
                    dogsRepository.storeDogsLocally(dogsList)
                    dogsMutableList.postValue(dogsList)
                }
            }
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

    private fun checkCacheDuration() {
        Timber.tag(_tag).d("checkCacheDuration")
        val cachePreference = dogsRepository.prefHelper.getCacheDuration()
        try {
            val cachePreferenceInt = cachePreference?.toInt() ?: 5 * 60
            refreshTime = cachePreferenceInt.times(1000 * 1000 * 1000L) // seconds to nano seconds
            Timber.tag(_tag).d("checkCacheDuration: new refreshTime=${refreshTime}")
        }
        catch (e: NumberFormatException) {
            Timber.tag(_tag).e("checkCacheDuration: bad number e=${e}")
        }
    }

    // display initial database creation for first app run
    fun setDogsList(dogsList: List<DogBreed>) {
        Timber.tag(_tag).d("setDogsList: size=${dogsList.size}")
        dogsMutableList.setValue(dogsList)
    }

    fun checkIfLoadingIsComplete() {
        Timber.tag(_tag).d("checkIfLoadingIsComplete")
        if (! didNetworkRefresh) {
            if (isNetworkAvailable()) {
                viewModelScope.launch {
                    dogsRepository.prefHelper.saveUpdateTime(0L)
                    refresh()
                }
            }
            else {
                Timber.tag(_tag).d("checkIfLoadingIsComplete: NO NETWORK")
            }
        }
        dogsMutableLoading.value = false
    }

    override fun onCleared() {
        Timber.tag(_tag).d("onCleared")
        super.onCleared()
        dogsRepository.onCleared()
    }

}
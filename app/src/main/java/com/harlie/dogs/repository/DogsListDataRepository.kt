package com.harlie.dogs.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.MyApplication
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.model.DogsApiService
import com.harlie.dogs.room.DogDatabase
import com.harlie.dogs.util.MyLiveDataReactiveStreams
import com.harlie.dogs.util.SharedPreferencesHelper
import com.harlie.dogs.util.default
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class DogsListDataRepository(val repositoryURL: String, val apiService: DogsApiService, val prefHelper: SharedPreferencesHelper): DataRepository() {
    private val _tag = "LEE: <" + DogsListDataRepository::class.java.simpleName + ">"

    init {
        Timber.tag(_tag).d("init $repositoryURL")
        apiService.setRealBaseUrl(repositoryURL)
    }

    fun fetchFromRemote(): LiveData<List<DogBreed>> {
        Timber.tag(_tag).d("fetchFromRemote")
        return MyLiveDataReactiveStreams.fromPublisher(
            apiService.getRequestApi()
                .getFlowableDogs()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
        )
    }

    suspend fun fetchFromDatabase(context: Context): LiveData<List<DogBreed>> {
        Timber.tag(_tag).d("fetchFromDatabase")
        val dogDao = DogDatabase.getInstance(context)!!.dogDao()
        val dogsList = dogDao.getAllDogs()
        return MutableLiveData<List<DogBreed>>().default(dogsList)
    }

    fun storeDogsLocally(dogsList: List<DogBreed>) {
        Timber.tag(_tag).d("storeDogsLocally")
        prefHelper.saveUpdateTime(System.nanoTime())
        databaseScope.launch {
            val context: Context = MyApplication.applicationContext()
            val dao = DogDatabase.getInstance(context)?.dogDao()
            dao?.deleteAllDogs() // since we are replacing the cache, delete old data first
            val result = dao?.insertAll(*dogsList.toTypedArray())
            result.let {
                var i = 0
                while (i < dogsList.size) {
                    dogsList[i].uuid = it?.get(i)?.toInt() ?: 0
                    ++i
                }
                prefHelper.markDatabaseCreated()
            }
        }
    }

    fun onCleared() {
        Timber.tag(_tag).d("onCleared")
        job.cancel()
    }

}

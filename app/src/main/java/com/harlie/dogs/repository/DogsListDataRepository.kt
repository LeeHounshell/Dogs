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

class DogsListDataRepository(repositoryURL: String): DataRepository() {
    private val _tag = "LEE: <" + DogsListDataRepository::class.java.simpleName + ">"

    private val dogsApiService = DogsApiService()
    private val prefHelper = SharedPreferencesHelper(MyApplication.applicationContext())

    init {
        Timber.tag(_tag).d("init $repositoryURL")
        dogsApiService.setRealBaseUrl(repositoryURL)
    }

    suspend fun fetchFromRemote(): LiveData<List<DogBreed>> {
        Timber.tag(_tag).d("fetchFromRemote")
        return MyLiveDataReactiveStreams.fromPublisher(
            dogsApiService.getRequestApi()
                .getFlowableDogs()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
        )
    }

    suspend fun fetchFromDatabase(): LiveData<List<DogBreed>> {
        Timber.tag(_tag).d("fetchFromDatabase")
        val dogsList = DogDatabase(MyApplication.applicationContext()).dogDao().getAllDogs()
        return MutableLiveData<List<DogBreed>>().default(dogsList)
    }

    fun storeDogsLocally(dogsList: List<DogBreed>) {
        Timber.tag(_tag).d("storeDogsLocally")
        databaseScope.launch {
            val context: Context = MyApplication.applicationContext()
            val dao = DogDatabase(context).dogDao()
            dao.deleteAllDogs()
            val result = dao.insertAll(*dogsList.toTypedArray())
            var i = 0
            while (i < dogsList.size) {
                dogsList[i].uuid = result[i].toInt()
                ++i
            }
            prefHelper.saveUpdateTime(System.nanoTime())
        }
    }

    fun onCleared() {
        Timber.tag(_tag).d("onCleared")
        job.cancel()
    }

}

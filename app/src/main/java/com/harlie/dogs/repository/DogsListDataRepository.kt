package com.harlie.dogs.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.model.DogsApiService
import io.reactivex.schedulers.Schedulers

class DogsListDataRepository(repositoryURL: String): DataRepository() {
    private val _tag = "LEE: <" + DogsListDataRepository::class.java.simpleName + ">"

    private val dogsApiService = DogsApiService()

    init {
        Timber.tag(_tag).d("init $repositoryURL")
        dogsApiService.setRealBaseUrl(repositoryURL)
    }

    fun fetchFromRemote(): LiveData<List<DogBreed>> {
        Timber.tag(_tag).d("fetchFromRemote")
        return LiveDataReactiveStreams.fromPublisher(
            dogsApiService.getRequestApi()
                .getFlowableDogs()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
        )
    }

}

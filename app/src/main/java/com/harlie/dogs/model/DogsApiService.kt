package com.harlie.dogs.model

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class DogsApiService {
    private val TAG = "LEE: <" + DogsApiService::class.java.simpleName + ">"

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com"
    }

    private lateinit var realBaseUrl: String
    private lateinit var api: DogsApi

    fun setRealBaseUrl(baseUrl: String) {
        Timber.tag(TAG).d("setRealBaseUrl: $baseUrl")
        realBaseUrl = baseUrl

        api = Retrofit.Builder()
            .baseUrl(realBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(DogsApi::class.java)
    }

    fun getDogs(): Single<List<DogBreed>> {
        Timber.tag(TAG).d("getDogs")
        return api.getDogs()
    }

}
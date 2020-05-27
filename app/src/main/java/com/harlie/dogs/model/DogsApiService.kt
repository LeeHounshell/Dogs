package com.harlie.dogs.model

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class DogsApiService {
    private val _tag = "LEE: <" + DogsApiService::class.java.simpleName + ">"

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com"
    }

    private lateinit var realBaseUrl: String
    private lateinit var requestApi: DogsApi

    fun setRealBaseUrl(baseUrl: String) {
        Timber.tag(_tag).d("setRealBaseUrl: $baseUrl")
        realBaseUrl = baseUrl

        requestApi = Retrofit.Builder()
            .baseUrl(realBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(DogsApi::class.java)
    }

    fun getRequestApi(): DogsApi {
        Timber.tag(_tag).d("getRequestApi")
        return requestApi
    }

}
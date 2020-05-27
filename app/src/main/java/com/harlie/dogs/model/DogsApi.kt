package com.harlie.dogs.model

import io.reactivex.Flowable
import retrofit2.http.GET

interface DogsApi {
    @GET("DevTides/DogsApi/master/dogs.json")
    fun getFlowableDogs(): Flowable<List<DogBreed>> // Flowable allows backpressure support
}
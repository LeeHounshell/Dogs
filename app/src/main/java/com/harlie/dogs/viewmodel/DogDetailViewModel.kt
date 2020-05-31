package com.harlie.dogs.viewmodel

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.Nullable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.MyApplication
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.repository.DogDetailDataRepository
import com.harlie.dogs.util.NotificationsHelper
import kotlinx.coroutines.launch


class DogDetailViewModel(private val repository: DogDetailDataRepository): MyViewModel() {
    private val tag = "LEE: <" + DogDetailViewModel::class.java.simpleName + ">"

    private val dogRepository: DogDetailDataRepository
    var isDeepLink: Boolean = false

    private val dogMutableDetail by lazy {
        MutableLiveData<DogBreed>()
    }

    init {
        Timber.tag(tag).d("init")
        dogRepository = repository
    }

    // Fragment Observers subscribe to this immutable LiveData
    val dog: LiveData<DogBreed>
        get() = dogMutableDetail

    suspend fun fetch() {
        Timber.tag(tag).d("fetch")
        viewModelScope.launch {
            fetchDogFromDatabase().observeForever { dog ->
                Timber.tag(tag).d("db observeForever dog_icon=${dog}")
                dogMutableDetail.postValue(dog)
                if (! isDeepLink) {
                    // create and run a Notification
                    sendNotificationWithDogImage(dog, dog.breedImageUrl)
                }
            }
        }
    }

    // Load bitmap from image url on background thread and display image notification
    private fun sendNotificationWithDogImage(dog: DogBreed, imageUrl: String?) {
        val bitmap = arrayOf<Bitmap?>(null)
        Glide.with(MyApplication.applicationContext())
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    bitmap[0] = resource
                    NotificationsHelper(MyApplication.applicationContext()).createNotification(dog, bitmap[0])
                }
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
            })
    }

    // Initiate a Database API call to get the DogBreed
    private suspend fun fetchDogFromDatabase(): LiveData<DogBreed> {
        Timber.tag(tag).d("fetchDogFromDatabase")
        return dogRepository.fetchFromDatabase()
    }

}

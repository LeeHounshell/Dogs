package com.harlie.dogs.viewmodel

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.telephony.SmsManager
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
import com.harlie.dogs.model.SmsInfo
import com.harlie.dogs.repository.DogDetailDataRepository
import com.harlie.dogs.util.NotificationsHelper
import com.harlie.dogs.util.RoomErrorEvent
import com.harlie.dogs.view.MainActivity
import kotlinx.coroutines.launch


class DogDetailViewModel(repository: DogDetailDataRepository): MyViewModel() {
    private val _tag = "LEE: <" + DogDetailViewModel::class.java.simpleName + ">"

    private val dogRepository: DogDetailDataRepository
    var isDeepLink: Boolean = false

    private val dogMutableDetail by lazy {
        MutableLiveData<DogBreed>()
    }

    init {
        Timber.tag(_tag).d("init")
        dogRepository = repository
        // prefetch from Room
        viewModelScope.launch {
            dogRepository.fetchFromDatabase(MyApplication.applicationContext())
        }
    }

    // Fragment Observers subscribe to this immutable LiveData
    val dog: LiveData<DogBreed>
        get() = dogMutableDetail

    suspend fun fetch() {
        Timber.tag(_tag).d("fetch")
        viewModelScope.launch {
            fetchDogFromDatabase().observeForever { dog ->
                if (dog != null) {
                    Timber.tag(_tag).d("db observeForever dog_icon=${dog}")
                    dogMutableDetail.postValue(dog)
                    if (!isDeepLink) {
                        // create and run a Notification
                        sendNotificationWithDogImage(dog, dog.breedImageUrl)
                    }
                }
                else {
                    Timber.tag(_tag).e("UNABLE TO LOAD DOGBREED FROM DATABASE!")
                    val roomErrorEvent = RoomErrorEvent("Unable to load Room dog data. Try again.")
                    roomErrorEvent.post()
                }
            }
        }
    }

    // Load bitmap from image url on background thread and display image notification
    private fun sendNotificationWithDogImage(dog: DogBreed, imageUrl: String?) {
        Timber.tag(_tag).d("sendNotificationWithDogImage")
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
        Timber.tag(_tag).d("fetchDogFromDatabase")
        return dogRepository.fetchFromDatabase(MyApplication.applicationContext())
    }

    // FIXME: SmsInfo has an imageUrl but MMS is required to send image data via text. The image is not sent.
    suspend fun sendSms(smsInfo: SmsInfo) {
        Timber.tag(_tag).d("sendSms smsInfo=${smsInfo}")
        val context = MyApplication.applicationContext()
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(smsInfo.to, null, smsInfo.text, pendingIntent, null)
    }

}

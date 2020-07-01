package com.harlie.dogs.viewmodel

import android.app.PendingIntent
import android.content.Intent
import android.telephony.SmsManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.MyApplication
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.model.SmsInfo
import com.harlie.dogs.repository.DogDetailDataRepository
import com.harlie.dogs.util.GlideWrapper
import com.harlie.dogs.util.RoomErrorEvent
import com.harlie.dogs.view.MainActivity
import kotlinx.coroutines.launch

class DogDetailViewModel(private val repository: DogDetailDataRepository): MyViewModel() {
    private val _tag = "LEE: <" + DogDetailViewModel::class.java.simpleName + ">"

    var isDeepLink: Boolean = false

    private val dogMutableDetail by lazy {
        MutableLiveData<DogBreed>()
    }

    init {
        Timber.tag(_tag).d("init")
        // prefetch from Room
        viewModelScope.launch {
            repository.fetchFromDatabase(MyApplication.applicationContext())
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
                        GlideWrapper().sendNotificationWithDogImage(dog, dog.breedImageUrl)
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

    // Initiate a Database API call to get the DogBreed
    private suspend fun fetchDogFromDatabase(): LiveData<DogBreed> {
        Timber.tag(_tag).d("fetchDogFromDatabase")
        return repository.fetchFromDatabase(MyApplication.applicationContext())
    }

    // FIXME: SmsInfo has an imageUrl but MMS is required to send image data via text. The image is not sent.
    fun sendSms(smsInfo: SmsInfo) {
        Timber.tag(_tag).d("sendSms smsInfo=${smsInfo}")
        val context = MyApplication.applicationContext()
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(smsInfo.to, null, smsInfo.text, pendingIntent, null)
    }

}

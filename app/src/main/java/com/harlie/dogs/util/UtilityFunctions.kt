package com.harlie.dogs.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import com.harlie.dogs.room.DogDatabase
import org.json.JSONArray
import java.io.InputStream

const val _tag = "LEE: <UtilityFunctions>"
const val PERMISSION_SEND_SMS = 234

fun getProgressDrawable(context: Context): CircularProgressDrawable {
    //Timber.tag(_tag).d("getProgressDrawable")
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 30f
        start()
    }
}

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, url: String?) {
    //Timber.tag(_tag).d("loadImage binding")
    GlideWrapper().loadImage(view, url, getProgressDrawable(view.context))
}

@BindingAdapter("bind:image_url")
fun loadCachedImage(view: ImageView, url: String?) {
    //Timber.tag(_tag).d("loadCachedImage binding")
    GlideWrapper().loadCachedImage(view, url)
}

// initialize LiveData from existing value(s)
fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }
fun <T : Any?> MutableLiveData<T>.postDefault(initialValue: T) = apply { postValue(initialValue) }

fun NavController.navigateSafe(direction: NavDirections) {
    Timber.tag(_tag).d("navigateSafe")
    currentDestination?.getAction(direction.actionId)?.let { navigate(direction) }
}

@Suppress("DEPRECATION")
fun isNetworkAvailable(context: Context?): Boolean {
    if (context == null) return false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Timber.tag(_tag).d("isNetworkAvailable true")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Timber.tag(_tag).d("isNetworkAvailable true")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Timber.tag(_tag).d("isNetworkAvailable true")
                    return true
                }
            }
        }
    }
    else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            Timber.tag(_tag).d("isNetworkAvailable true")
            return true
        }
    }
    Timber.tag(_tag).d("isNetworkAvailable false")
    return false
}

inline fun <reified T>extractArrayFromJson(rawJson: String?): MutableList<T> {
    val list: MutableList<T> = mutableListOf()
    if (rawJson != null) {
        val array = JSONArray(rawJson)
        Timber.tag(DogDatabase._tag).d("extractArrayFromJson: processing length=${array.length()}")
        if (array.length() > 0) {
            val gson = Gson()
            var i = 0
            while (i < array.length()) {
                list.add(
                    gson.fromJson(
                        array.getJSONObject(i).toString(),
                        T::class.java
                    )
                )
                i++
            }
        }
    }
    return list
}

fun readJsonAsset(context: Context, fileName: String): String? {
    Timber.tag(DogDatabase._tag).d("readJsonAsset: fileName=${fileName}")
    var rawJson: String? = null
    try {
        val inputStream: InputStream = context.assets.open(fileName)
        rawJson = inputStream.bufferedReader().use { it.readText() }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return rawJson
}


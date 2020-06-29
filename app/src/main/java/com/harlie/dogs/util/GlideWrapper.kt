package com.harlie.dogs.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.palette.graphics.Palette
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.MyApplication
import com.harlie.dogs.R
import com.harlie.dogs.databinding.FragmentDetailBinding
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.model.DogPalette
import com.harlie.dogs.view.DetailFragment

class GlideWrapper {
    private val _tag = "LEE: <" + GlideWrapper::class.java.simpleName + ">"

    companion object {
        // an alternative way to using dependency injection for GlideWrapper
        var isUnitTest: Boolean = false
    }

    fun loadImage(view: ImageView, uri: String?, progressDrawable: CircularProgressDrawable) {
        //Timber.tag(_tag).d("loadImage")
        if (isUnitTest) return
        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(progressDrawable)
            .error(R.mipmap.ic_dog_icon)

        Glide.with(view.context)
            .setDefaultRequestOptions(options)
            .load(uri)
            .into(view)
    }

    // when image is cached locally no need for .placeholder or .error
    fun loadCachedImage(view: ImageView, uri: String?) {
        //Timber.tag(_tag).d("loadCachedImage")
        if (isUnitTest) return
        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

        Glide.with(view.context)
            .setDefaultRequestOptions(options)
            .load(uri)
            .into(view)
    }

    fun setBackgroundColor(frag: DetailFragment, dataBinding: FragmentDetailBinding, url: String) {
        Timber.tag(_tag).d("setBackgroundColor")
        if (isUnitTest) return
        Glide.with(frag)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    Palette.from(resource)
                        .generate { palette ->
                            val intColor = palette?.vibrantSwatch?.rgb ?: 0
                            val myPalette = DogPalette(intColor)
                            dataBinding.palette = myPalette
                        }
                }
            })
    }

    // Load bitmap from image url on background thread and display image notification
    fun sendNotificationWithDogImage(dog: DogBreed, imageUrl: String?) {
        Timber.tag(_tag).d("sendNotificationWithDogImage")
        if (isUnitTest) return
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
                    NotificationsHelper(MyApplication.applicationContext()).createNotification(
                        dog,
                        bitmap[0]
                    )
                }

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
            })
    }

}
package com.harlie.dogs.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.navigation.NavDeepLinkBuilder
import com.github.ajalt.timberkt.Timber
import com.harlie.dogs.R
import com.harlie.dogs.model.DogBreed
import com.harlie.dogs.view.MainActivity

class NotificationsHelper(val context: Context) {
    private val _tag = "LEE: <" + NotificationsHelper::class.java.simpleName + ">"

    private val CHANNEL_ID = "dogs_channel_id"

    fun createNotification(dog: DogBreed, icon: Bitmap?) {
        Timber.tag(_tag).d("createNotification")
        createNotificationChannel()

        // pass a Navigation argument to the DetailFragment
        val bundle: Bundle = Bundle()
        bundle.putInt("dog_Uuid", dog.uuid)
        bundle.putBoolean("deep_link", true)

        // use NavDeepLinkBuilder to open a specific destination
        val pendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.dog_navigation)
            .setDestination(R.id.detailFragment)
            .setArguments(bundle)
            .createPendingIntent()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_dog_icon)
            .setLargeIcon(icon)
            .setContentTitle("${dog.breedName}")
            .setContentText(if (dog.breedPurpose != null) "Used for ${dog.breedPurpose}" else "")
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(icon)
                    .bigLargeIcon(null)
            )
            .setContentIntent(
                // this will create a DeepLink into the DetailFragment including a back stack
                TaskStackBuilder.create(context).run {
                    addNextIntentWithParentStack(Intent(context, MainActivity::class.java).apply {
                        putExtras(bundle)
                    })
                    pendingIntent
                }
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val NOTIFICATION_ID = dog.uuid // so we have a different Notification for each Detail view
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        Timber.tag(_tag).d("createNotificationChannel")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_ID
            val descriptionText = "Dogs Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
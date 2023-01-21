package com.code.korti.seaofimages.presentation.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat

object NotificationChannels {
    const val DOWNLOAD_CHANNEL_ID = "download"

    fun create(context: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createDownloadChannel(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDownloadChannel(context: Context){
        val name = "Download"
        val priority = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(DOWNLOAD_CHANNEL_ID, name, priority)
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }
}
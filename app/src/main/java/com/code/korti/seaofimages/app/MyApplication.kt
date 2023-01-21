package com.code.korti.seaofimages.app

import android.app.Application
import com.code.korti.seaofimages.presentation.download.NotificationChannels
import com.code.korti.seaofimages.data.db.Database
import timber.log.Timber

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Database.init(this)
        Timber.plant(Timber.DebugTree())
        NotificationChannels.create(this)
    }
}
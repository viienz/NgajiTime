package com.example.ngajitime

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NgajiTimeApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
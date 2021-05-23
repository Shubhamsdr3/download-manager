package com.example.customdownloadmanger

import android.app.Application
import timber.log.Timber

class App: Application() {

    companion object {
        private var INSTANCE : App ? = null

        fun getInstance(): App {
            if (INSTANCE == null) {
                INSTANCE = App()
            }
            return INSTANCE!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
package io.github.ghostbuster91.postponeit

import android.app.Application

class PostponeItApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        contextProvider = { applicationContext }
    }
}
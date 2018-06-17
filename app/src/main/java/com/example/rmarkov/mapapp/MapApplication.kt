package com.example.rmarkov.mapapp

import android.app.Application
import com.example.rmarkov.mapapp.dagger.components.ApplicationComponent
import com.example.rmarkov.mapapp.dagger.components.DaggerApplicationComponent
import com.example.rmarkov.mapapp.dagger.modules.ApplicationModule

class MapApplication: Application() {

    lateinit var component: ApplicationComponent

    override fun onCreate() {
        component = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
        super.onCreate()
    }
}
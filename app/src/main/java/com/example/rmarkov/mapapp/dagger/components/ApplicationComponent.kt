package com.example.rmarkov.mapapp.dagger.components

import com.example.rmarkov.mapapp.LocationService
import com.example.rmarkov.mapapp.MainActivity
import com.example.rmarkov.mapapp.dagger.modules.ApplicationModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component (modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(locationService: LocationService)
}
package com.example.rmarkov.mapapp.dagger.components

import com.example.rmarkov.mapapp.SettingsActivity
import com.example.rmarkov.mapapp.location.LocationService
import com.example.rmarkov.mapapp.map.MapActivity
import com.example.rmarkov.mapapp.dagger.modules.ApplicationModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component (modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    fun inject(activity: MapActivity)
    fun inject(locationService: LocationService)
    fun inject(settingsFragment: SettingsActivity.SettingsFragment)
}
package com.example.rmarkov.mapapp.dagger.modules

import android.content.Context
import com.example.rmarkov.mapapp.location.LocationStatusHolder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val context: Context) {

    @Singleton
    @Provides
    public fun provideContext(): Context {
        return this.context;
    }

    @Singleton
    @Provides
    public fun provideFusedLocationProviderClient(): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context);
    }

    @Singleton
    @Provides
    public fun provideLocationStatusHolder(): LocationStatusHolder {
        return LocationStatusHolder();
    }
}
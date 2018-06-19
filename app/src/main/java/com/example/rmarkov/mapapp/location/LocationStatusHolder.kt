package com.example.rmarkov.mapapp.location

import com.example.rmarkov.mapapp.utils.distanceTo
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Singleton

@Singleton
class LocationStatusHolder {

    lateinit var deviceLocation: LatLng
        private set
    var destinationLocation: LatLng? = null
        private set

    val deviceLocationServiceSubject: Subject<LatLng> = PublishSubject.create()
    val deviceLocationObservable: Observable<LatLng> = deviceLocationServiceSubject

    val destinationLocationSubject: Subject<LatLng> = PublishSubject.create()
    val destinationLocationObservable: Observable<LatLng> = destinationLocationSubject

    fun onDevicePositionChanged(latlng: LatLng) {
        deviceLocation = latlng
        deviceLocationServiceSubject.onNext(latlng)
    }

    fun onDestinationPositionChanged(latlng: LatLng) {
        destinationLocation = latlng
        destinationLocationSubject.onNext(latlng)
    }

    public fun validateNewRadius(newRadius: Double): Boolean {
        val distance = deviceLocation.distanceTo(destinationLocation)
        return newRadius < distance
    }
}
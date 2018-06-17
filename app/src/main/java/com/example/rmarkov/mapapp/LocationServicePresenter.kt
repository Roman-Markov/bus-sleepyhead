package com.example.rmarkov.mapapp

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.example.rmarkov.mapapp.dagger.BasePresenter
import com.example.rmarkov.mapapp.utils.checkLocationPermission
import com.example.rmarkov.mapapp.utils.toLatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import javax.inject.Inject

class LocationServicePresenter
@Inject constructor(val context: Context,
                    private val fusedLocationProviderClient: FusedLocationProviderClient,
                    private val locationHolder: LocationStatusHolder): BasePresenter<ILocationService>(){

    lateinit var lastKnownLocation: Location

    private var locationRequest: LocationRequest? = null

    var locationCallback: LocationCallback = object: LocationCallback() {
        override fun onLocationResult(locactioResult: LocationResult?) {
            if (locactioResult == null) return
            lastKnownLocation = locactioResult.lastLocation
            // TODO think about delegate
            locationHolder.onDevicePositionChanged(lastKnownLocation.toLatLng())
        }
    }

    @SuppressLint("MissingPermission")
    fun onServiceStarted() {
        if (context.checkLocationPermission()) {
            // first retrieving location here to faster pass to MapView, since LocationCallback may
            // be called too late
            getDeviceLocation()
            populateLocationRequest()
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            Toast.makeText(context, "Location permission is needed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun detachView() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.detachView()
    }

    private fun getDeviceLocation() {
        try {
            if (context.checkLocationPermission()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(object: OnCompleteListener<Location> {
                    override fun onComplete(task: Task<Location>) {
                        if (task.isSuccessful) {
                            lastKnownLocation = task.result
                            locationHolder.onDevicePositionChanged(lastKnownLocation.toLatLng())
                        } else {
                            Log.e(MainActivity.TAG, "Current location is null")
                        }
                    }

                })
            }
        } catch (e: SecurityException) {
            Log.e(MainActivity.TAG, "Exception: ${e.localizedMessage}")
        }
    }

    private fun populateLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = 5000
        locationRequest!!.fastestInterval = 5000
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
}
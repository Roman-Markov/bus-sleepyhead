package com.example.rmarkov.mapapp.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.preference.PreferenceManager
import android.util.Log
import com.example.rmarkov.mapapp.BasePresenter
import com.example.rmarkov.mapapp.Constants
import com.example.rmarkov.mapapp.R
import com.example.rmarkov.mapapp.utils.checkLocationPermission
import com.example.rmarkov.mapapp.utils.distanceTo
import com.example.rmarkov.mapapp.utils.toLatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.pavelsikun.seekbarpreference.SeekBarPreferenceCompat
import javax.inject.Inject

class LocationServicePresenter
@Inject constructor(val context: Context,
                    private val fusedLocationProviderClient: FusedLocationProviderClient,
                    private val locationHolder: LocationStatusHolder): BasePresenter<ILocationService>(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = "LocationServPresenter"

    private lateinit var lastKnownLocation: Location

    private var lastKnownDestination: LatLng? = null

    private var locationRequest: LocationRequest? = null

    private var radius = Constants.DEFAULT_RADIUS

    private var isLocationUpdatesRequested = false

    var locationCallback: LocationCallback = object: LocationCallback() {
        override fun onLocationResult(locactioResult: LocationResult?) {
            if (locactioResult == null) return
            lastKnownLocation = locactioResult.lastLocation
            // TODO think about delegate
            locationHolder.onDevicePositionChanged(lastKnownLocation.toLatLng())
            handleDeviceLocation(lastKnownLocation.toLatLng())
        }
    }

    @SuppressLint("MissingPermission")
    fun onServiceStarted(lastDestination: LatLng?) {
        lastKnownDestination = lastDestination?: lastKnownDestination
        if (!isLocationUpdatesRequested) {
            if (context.checkLocationPermission()) {
                Log.d(TAG, "permissions exists, starting work...")
                // first retrieving location here to faster pass to MapView, since LocationCallback may
                // be called too late
                getDeviceLocation()
                populateLocationRequest()
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
                isLocationUpdatesRequested = true
            } else {
                Log.d(TAG, "Location permission is needed")
            }
        }
    }

    override fun attachView(view: ILocationService) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .registerOnSharedPreferenceChangeListener(this)
        radius = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(context.getString(R.string.preference_key_for_radius), 1000)
                .toDouble()
        super.attachView(view)
    }

    override fun detachView() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        PreferenceManager.getDefaultSharedPreferences(context)
                .unregisterOnSharedPreferenceChangeListener(this)
        isLocationUpdatesRequested = false
        super.detachView()
        if (lastKnownDestination == null) {
            // it is no need to keep service in background when destination is unknown
            view?.stop()
        }
    }

    private fun getDeviceLocation() {
        Log.d(TAG, "retrieving current location...")
        try {
            if (context.checkLocationPermission()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(object: OnCompleteListener<Location> {
                    override fun onComplete(task: Task<Location>) {
                        if (task.isSuccessful) {
                            Log.d(TAG, "retrieving current location is success: ${task.result}")
                            lastKnownLocation = task.result
                            locationHolder.onDevicePositionChanged(lastKnownLocation.toLatLng())
                        } else {
                            Log.e(TAG, "Current location is null, exception: ", task.exception)
                        }
                    }
                })
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Exception: $e")
        }
    }

    private fun populateLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = 5000
        locationRequest!!.fastestInterval = 5000
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun handleDeviceLocation(latlng: LatLng) {
        Log.d(TAG, "location: $latlng")
        if (lastKnownDestination != null) {
            handleNewDistance(latlng.distanceTo(lastKnownDestination))
        }
    }

    private fun handleNewDistance(distance: Float) {
        Log.d(TAG, "distance: $distance")
        if (distance < radius) {
            Log.d(TAG, "Start alert - distance: $distance, radius: $radius")
            view?.startAlert()
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == context.getString(R.string.preference_key_for_radius)) {
            radius = PreferenceManager.getDefaultSharedPreferences(context)
                    .getInt(context.getString(R.string.preference_key_for_radius), 1000)
                    .toDouble()
        }
    }
}
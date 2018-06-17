package com.example.rmarkov.mapapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.util.Log
import com.example.rmarkov.mapapp.dagger.BasePresenter
import com.example.rmarkov.mapapp.utils.distanceTo
import com.example.rmarkov.mapapp.utils.toLatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import javax.inject.Inject
import android.widget.Toast
import android.app.AlarmManager
import android.content.Context.ALARM_SERVICE
import android.app.PendingIntent
import android.content.Intent
import android.os.Vibrator
import android.content.BroadcastReceiver





class MapViewPresenter
@Inject constructor(private val context: Context,
                    private var fusedLocationProviderClient: FusedLocationProviderClient)
    : BasePresenter<IMapView>(), GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {


    override fun onMarkerDragStart(p0: Marker?) {
        // currently do nothing
    }

    override fun onMarkerDrag(p0: Marker?) {
        // currently do nothing
    }

    override fun onMarkerDragEnd(m: Marker?) {
        if (m != null && m.id == marker?.id) {
            marker = m
            circle?.remove()
            circle = view?.addCircle(createUsualCircleOptions(m.position))
            handleNewDistance(m.position.distanceTo(lastKnownLocation.toLatLng()))
        }
    }

    lateinit var lastKnownLocation: Location

    private var locationRequest: LocationRequest? = null

    var marker: Marker? = null

    var circle: Circle? = null

    var locationCallback: LocationCallback = object: LocationCallback() {
        override fun onLocationResult(locactioResult: LocationResult?) {
            if (locactioResult == null) return
            lastKnownLocation = locactioResult.lastLocation
            val v = view
            v?: return
            val temp = marker
            var distance: Float? = null
            temp?.let {
                distance = lastKnownLocation.distanceTo(temp.position)}
            v.showLocationInfo(lastKnownLocation.toLatLng())
            v.showDistance(distance?:-2000f)
        }
    }

    public override fun attachView(view: IMapView) {
        super.attachView(view)
    }

    public override fun detachView() {
        super.detachView()
    }

    fun onMapReady() {
        view?.updateLocationUi()
        getDeviceLocation()
        startLocationUpdate()
    }

    private fun getDeviceLocation() {
        try {
            val v = view
            if (v != null && v.isLocationPermissionGranted()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(object: OnCompleteListener<Location> {
                    override fun onComplete(task: Task<Location>) {
                        if (task.isSuccessful) {
                            v.showLocationInfo(task.result.toLatLng());
                            lastKnownLocation = task.result
                            v.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation.toLatLng(), 12f))
                        } else {
                            Log.e(MainActivity.TAG, "Current location is null")
                            v.disableLocationUi()
                        }
                    }

                })
            }
        } catch (e: SecurityException) {
            Log.e(MainActivity.TAG, "Exception: ${e.localizedMessage}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        val v = view
        v?: return
        if (v.isLocationPermissionGranted()) {
            populateLocationRequest()
            // TODO make sure that lastKnownLocation is not null
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            v.getLocationPermission();
        }
    }

    private fun populateLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = 5000
        locationRequest!!.fastestInterval = 5000
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun onPause() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onMapClick(latlng: LatLng?) {
        val map = view?: return
        marker?.remove()
        circle?.remove()
        latlng?.let{
            val m = map.createMarker(MarkerOptions()
                    .position(latlng)
                    .title("Destination")
                    .draggable(true))
            marker = m
            circle = map.addCircle(createUsualCircleOptions(latlng))
            handleNewDistance(m.position.distanceTo(lastKnownLocation.toLatLng()))
        }
    }

    private fun createUsualCircleOptions(latlng: LatLng): CircleOptions {
        return CircleOptions()
                .center(latlng)
                .radius(1000.0)
                .strokeColor(Color.BLUE)
                .strokeWidth(1f)
                .fillColor(context.resources.getColor(R.color.circle))
    }

    private fun handleNewDistance(distance: Float) {
        if (distance > 1000) {
            view?.showDistance(distance)
        } else {
            view?.startAlert()
        }
    }

}

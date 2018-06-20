package com.example.rmarkov.mapapp.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.preference.PreferenceManager
import android.util.Log
import com.example.rmarkov.mapapp.location.LocationStatusHolder
import com.example.rmarkov.mapapp.R
import com.example.rmarkov.mapapp.BasePresenter
import com.example.rmarkov.mapapp.Constants
import com.example.rmarkov.mapapp.utils.distanceTo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import javax.inject.Inject


class MapViewPresenter
@Inject constructor(private val context: Context,
                    private val locationStatusHolder: LocationStatusHolder)
    : BasePresenter<IMapView>(), GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener, SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        const val TAG = "MapViewPresenter"
    }

    var isFirstLocation: Boolean = true

    var previousMarkerLocation: LatLng? = null

    var marker: Marker? = null

    var circle: Circle? = null

    var radius = Constants.DEFAULT_RADIUS

    override fun attachView(view: IMapView) {
        super.attachView(view)
        PreferenceManager.getDefaultSharedPreferences(context)
                .registerOnSharedPreferenceChangeListener(this)
        radius = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(context.getString(R.string.preference_key_for_radius), 1000)
                .toDouble()

        updateCircle()

        val deviceDistanceDisposable = locationStatusHolder
                .deviceLocationObservable
                .subscribe(this::handleDeviceLocation)
        compositeDisposable.add(deviceDistanceDisposable)
    }

    public override fun detachView() {
        PreferenceManager.getDefaultSharedPreferences(context)
                .unregisterOnSharedPreferenceChangeListener(this)
        super.detachView()
    }

    fun onMapReady() {
        view?.updateLocationUi()
        startLocationUpdate()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        val v = view
        v?: return
        if (v.isLocationPermissionGranted()) {
            v.startLocationService(null)
        } else {
            v.getLocationPermission();
        }
    }

    override fun onMarkerDragStart(m: Marker?) {
        if (m != null && m.id == marker?.id) {
            previousMarkerLocation = marker?.position
        }
    }

    override fun onMarkerDrag(p0: Marker?) {
        // currently do nothing
    }

    override fun onMarkerDragEnd(m: Marker?) {
        Log.d(TAG, "marker drag end: " + m?.position)
        if (m != null && m.id == marker?.id) {
            handleNewDraggableMarker(m)
        }
    }

    override fun onMapClick(latlng: LatLng?) {
        Log.d(TAG, "map clicked: $latlng")
        latlng?.let {
            if (latlng.distanceTo(locationStatusHolder.deviceLocation) > radius) {
                marker?.remove()
                circle?.remove()
                createNewDestination(latlng)
            } else {
                view?.showMessage(R.string.destination_is_too_close)
                // TODO think about creating default marker
            }
        }
    }

    private fun createNewDestination(latlng: LatLng) {
        Log.d(TAG, "creating new destination: $latlng")
        locationStatusHolder.onDestinationPositionChanged(latlng)
        val map = view?: return
        map.startLocationService(latlng)
        marker = map.createMarker(createUsualMarkerOptions(latlng))
        circle = map.addCircle(createUsualCircleOptions(latlng))
    }

    private fun createUsualMarkerOptions(latlng: LatLng): MarkerOptions {
        return MarkerOptions()
                .position(latlng)
                .title("Destination")
                .draggable(true)
    }

    private fun createUsualCircleOptions(latlng: LatLng?): CircleOptions {
        return CircleOptions()
                .center(latlng)
                .radius(radius)
                .strokeColor(Color.BLUE)
                .strokeWidth(1f)
                .fillColor(context.resources.getColor(R.color.circle))
    }

    // previous position of marker that was dragged is correct
    private fun handleNewDraggableMarker(m: Marker) {
        val map = view?: return
        val distance = locationStatusHolder.deviceLocation.distanceTo(m.position)
        if (distance > radius) {
            locationStatusHolder.onDestinationPositionChanged(m.position)
            circle?.remove()
            circle = map.addCircle(createUsualCircleOptions(m.position))
            map.startLocationService(m.position)
        } else {
            // return to previous marker
            m.remove()
            marker = map.createMarker(createUsualMarkerOptions(circle!!.center))
            map.showMessage(R.string.destination_is_too_close)
        }
    }

    private fun handleDeviceLocation(latlng: LatLng) {
        if (isFirstLocation) {
            isFirstLocation = false
            view?.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12f))
        }
        if (locationStatusHolder.destinationLocation != null) {
            handleNewDistance(latlng.distanceTo(locationStatusHolder.destinationLocation))
        }
    }

    private fun handleNewDistance(distance: Float) {
            view?.showDistance(distance)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == context.getString(R.string.preference_key_for_radius)) {
            sharedPreferences?.let {
                radius = sharedPreferences.getInt(context.getString(R.string.preference_key_for_radius), 1000).toDouble()}
            Log.d(TAG, "radius has changed from settings: $radius")
            updateCircle()
        }
    }

    private fun updateCircle() {
        circle?.remove()
        val previousDestination = marker?.position
        previousDestination?.let{
            circle = view?.addCircle(createUsualCircleOptions(previousDestination))}
    }

}

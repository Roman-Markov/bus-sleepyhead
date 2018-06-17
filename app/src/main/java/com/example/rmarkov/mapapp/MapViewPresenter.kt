package com.example.rmarkov.mapapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import com.example.rmarkov.mapapp.dagger.BasePresenter
import com.example.rmarkov.mapapp.utils.distanceTo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import javax.inject.Inject


class MapViewPresenter
@Inject constructor(private val context: Context,
                    private val locationStatusHolder: LocationStatusHolder)
    : BasePresenter<IMapView>(), GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {

    var isFirstLocation: Boolean = true

    var previousMarkerLocation: LatLng? = null

    var marker: Marker? = null

    var circle: Circle? = null

    var radius = 1000.0

    public override fun attachView(view: IMapView) {
        val deviceDistanceDisposable = locationStatusHolder
                .deviceLocationObservable
                .subscribe(this::handleDeviceLocation)
        compositeDisposable.add(deviceDistanceDisposable)
        super.attachView(view)
    }

    public override fun detachView() {
        super.detachView()
    }

    fun onMapReady() {
        view?.startLocationService()
        view?.updateLocationUi()
        startLocationUpdate()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdate() {
        val v = view
        v?: return
        if (v.isLocationPermissionGranted()) {
            v.startLocationService()
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
        if (m != null && m.id == marker?.id) {
            handleNewDraggableMarker(m)
        }
    }

    override fun onMapClick(latlng: LatLng?) {
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
        val map = view?: return
        marker = map.createMarker(createUsualMarkerOptions(latlng))
        circle = map.addCircle(createUsualCircleOptions(latlng))
    }

    private fun createUsualMarkerOptions(latlng: LatLng): MarkerOptions {
        return MarkerOptions()
                .position(latlng)
                .title("Destination")
                .draggable(true)
    }

    private fun createUsualCircleOptions(latlng: LatLng): CircleOptions {
        return CircleOptions()
                .center(latlng)
                .radius(radius)
                .strokeColor(Color.BLUE)
                .strokeWidth(1f)
                .fillColor(context.resources.getColor(R.color.circle))
    }

    // previous position of marker that was dragged is correct
    private fun handleNewDraggableMarker(m: Marker) {
        val v = view?: return
        val distance = locationStatusHolder.deviceLocation.distanceTo(m.position)
        if (distance > radius) {
            locationStatusHolder.onDestinationPositionChanged(m.position)
            circle?.remove()
            circle = v.addCircle(createUsualCircleOptions(m.position))
        } else {
            // return to previous marker
            m.remove()
            marker = v.createMarker(createUsualMarkerOptions(circle!!.center))
            v.showMessage(R.string.destination_is_too_close)
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
        if (distance > radius) {
            view?.showDistance(distance)
        } else {
            view?.startAlert()
        }
    }

}

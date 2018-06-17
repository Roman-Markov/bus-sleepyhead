package com.example.rmarkov.mapapp

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.*

public interface IMapView {
    fun updateLocationUi()

    fun showLocationInfo(latLng: LatLng)

    fun showDistance(distance: Float)

    fun isLocationPermissionGranted(): Boolean

    fun getLocationPermission()

    fun moveCamera(cameraUpdate: CameraUpdate)

    fun enableLocationUi()

    fun disableLocationUi()

    fun clearAll();

    fun createMarker(markerOptions: MarkerOptions): Marker

    fun addCircle(circleOptions: CircleOptions): Circle

    fun startAlert()
}
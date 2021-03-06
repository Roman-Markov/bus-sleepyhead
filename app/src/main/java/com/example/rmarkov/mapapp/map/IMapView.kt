package com.example.rmarkov.mapapp.map

import android.support.annotation.IdRes
import android.support.annotation.IntegerRes
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

    fun startLocationService(latlng: LatLng?)

    fun showMessage(stringIdRes: Int)
}
@file:JvmName("Utils")

package com.example.rmarkov.mapapp.utils

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.content.ContextCompat
import com.example.rmarkov.mapapp.MapApplication
import com.example.rmarkov.mapapp.dagger.components.ApplicationComponent
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng


fun Location.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun LocationResult.toLatLng(): LatLng {
    return lastLocation.toLatLng()
}

fun LatLng.distanceTo(latlng: LatLng?): Float {
    if (latlng == null) return -2000f
    val locationA = Location("A")
    locationA.latitude = latitude
    locationA.longitude = longitude
    val locationB = Location("B")
    locationB.latitude = latlng.latitude
    locationB.longitude = latlng.longitude
    return locationA.distanceTo(locationB)
}

fun Location.distanceTo(latlng: LatLng?): Float {
    latlng?: return -2000f
    val locationB = Location("B")
    locationB.latitude = latlng.latitude
    locationB.longitude = latlng.longitude
    return distanceTo(locationB)
}

fun Context.getAppComponent(): ApplicationComponent {
    return (applicationContext as MapApplication).component
}

fun Context.checkLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
}

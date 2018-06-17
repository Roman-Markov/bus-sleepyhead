@file:JvmName("Utils")

package com.example.rmarkov.mapapp.utils

import android.location.Location
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

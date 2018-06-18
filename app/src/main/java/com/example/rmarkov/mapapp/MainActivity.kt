package com.example.rmarkov.mapapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Vibrator
import android.support.annotation.IdRes
import android.support.annotation.IntegerRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject


class MainActivity : Activity(), IMapView, OnMapReadyCallback {

    companion object {
        val TAG = "MainActivity"
        val REQUEST_CODE_FOR_LOCATION_PERMISSIONS = 1421
    }

    lateinit var googleMap: GoogleMap;
    @Inject
    lateinit var presenter: MapViewPresenter

    lateinit var marker: Marker

    var isPermissionGranted = false;

    override fun onStart() {
        presenter.attachView(this)
        super.onStart()
    }

    override fun onStop() {
        presenter.detachView()
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (fragmentManager.findFragmentById(R.id.mapView) as MapFragment).getMapAsync(this)
        (applicationContext as MapApplication).component.inject(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        if (map != null) {
            googleMap = map
            googleMap.setOnMarkerDragListener(presenter)
            googleMap.setOnMapClickListener(presenter)
            presenter.onMapReady()
        }
    }

    @SuppressLint("MissingPermission")
    override fun updateLocationUi() {
        if (isPermissionGranted) {
            enableLocationUi()
        } else {
            disableLocationUi()
            getLocationPermission()
        }
    }



    override fun showLocationInfo(latLng: LatLng) {
        //locationTv.text = latLng.toString()
    }

    override fun showDistance(distance: Float) {
        locationTv.text = String.format("Distance: %.1f", distance)
    }

    override fun isLocationPermissionGranted(): Boolean = isPermissionGranted

    override  fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            isPermissionGranted = true
            updateLocationUi()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_CODE_FOR_LOCATION_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        isPermissionGranted = false
        when (requestCode) {
            REQUEST_CODE_FOR_LOCATION_PERMISSIONS -> if (grantResults != null
                    && grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true
            }
        }
    }

    override fun moveCamera(cameraUpdate: CameraUpdate) {
        googleMap.moveCamera(cameraUpdate)
    }

    @SuppressLint("MissingPermission")
    override fun enableLocationUi() {
        if (isPermissionGranted) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        }
    }

    @SuppressLint("MissingPermission")
    override fun disableLocationUi() {
        if (isPermissionGranted) {
            googleMap.isMyLocationEnabled = false
            googleMap.uiSettings.isMyLocationButtonEnabled = false
        }
    }

    override fun clearAll() {
        googleMap.clear()
    }

    override fun createMarker(markerOptions: MarkerOptions): Marker {
        return googleMap.addMarker(markerOptions)
    }

    override fun addCircle(circleOptions: CircleOptions): Circle {
        return googleMap.addCircle(circleOptions)
    }

    override fun startLocationService(latlng: LatLng?) {
        if (latlng == null) {
            startService(Intent(this, LocationService::class.java))
        } else {
            val intent = LocationService
                    .createIntent(this)
                    .putExtra(this.getString(R.string.key_for_last_destination), arrayListOf(latlng))
            startService(intent);
        }
    }

    override fun showMessage(stringIdRes: Int) {
        Toast.makeText(this, stringIdRes, Toast.LENGTH_SHORT).show()
    }
}

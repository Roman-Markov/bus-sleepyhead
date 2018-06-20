package com.example.rmarkov.mapapp.map

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.rmarkov.mapapp.MapApplication
import com.example.rmarkov.mapapp.R
import com.example.rmarkov.mapapp.SettingsActivity
import com.example.rmarkov.mapapp.location.LocationService
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MapActivity : AppCompatActivity(), IMapView, OnMapReadyCallback {

    companion object {
        val TAG = "MapActivity"
        val REQUEST_CODE_FOR_LOCATION_PERMISSIONS = 1421
    }

    private lateinit var googleMap: GoogleMap;
    @Inject
    lateinit var presenter: MapViewPresenter

    private var isPermissionGranted = false;

    private lateinit var settingsItem: MenuItem

    override fun onStart() {
        presenter.attachView(this)
        super.onStart()
    }

    override fun onStop() {
        presenter.detachView()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        menu?.let{ settingsItem = menu.findItem(R.id.settings)}
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == settingsItem.itemId) {
            startActivity(SettingsActivity.createIntent(this))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment).getMapAsync(this)
        (applicationContext as MapApplication).component.inject(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        Log.d(TAG, "map is ready")
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

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        isPermissionGranted = false
        when (requestCode) {
            REQUEST_CODE_FOR_LOCATION_PERMISSIONS -> if (grantResults.isNotEmpty()
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
        Log.d(TAG, "createMarker at position ${markerOptions.position}")
        return googleMap.addMarker(markerOptions)
    }

    override fun addCircle(circleOptions: CircleOptions): Circle {
        Log.d(TAG, "addCircle at position ${circleOptions.center}")
        return googleMap.addCircle(circleOptions)
    }

    override fun startLocationService(latlng: LatLng?) {
        Log.d(TAG, "startLocationService for tracking destination $latlng")
        if (latlng == null) {
            startService(Intent(this, LocationService::class.java))
        } else {
            val intent = LocationService.createIntent(this)
                    .putExtra(this.getString(R.string.key_for_last_destination), arrayListOf(latlng))
            startService(intent);
        }
    }

    override fun showMessage(stringIdRes: Int) {
        Toast.makeText(this, stringIdRes, Toast.LENGTH_SHORT).show()
    }
}

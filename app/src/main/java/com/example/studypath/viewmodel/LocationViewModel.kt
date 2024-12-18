package com.example.studypath.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.location.LocationServices


//CODE FROM BUGURA
class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application
    var lastLocation by mutableStateOf<Location?>(null)

    @OptIn(ExperimentalPermissionsApi::class)
    private var locationPermissionState: MultiplePermissionsState? = null


    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(app)

        if (!hasPermission()) return

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    lastLocation = location
                }
            }
            .addOnFailureListener { exception ->
                Log.d("LocationViewModel", "Location not found: $exception")

            }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun updatePermissionState(permissionState: MultiplePermissionsState) {
        locationPermissionState = permissionState
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun hasPermission(): Boolean {
        var hasPermission = false

        for (element in locationPermissionState?.permissions!!) {
            if (element.status.isGranted) hasPermission = true
        }

        if (hasPermission) {
            val locationManager = app.getSystemService(LOCATION_SERVICE) as LocationManager

            hasPermission =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
                )
        }

        return hasPermission
    }
}
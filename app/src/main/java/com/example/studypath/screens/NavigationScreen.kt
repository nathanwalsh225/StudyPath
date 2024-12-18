package com.example.studypath.screens

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studypath.navigation.BottomAppBar
import com.example.studypath.navigation.MainScreenWithSidebar
import com.example.studypath.viewmodel.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavigationScreen(
    navController: NavController,
    userEmail: String,
    userName: String,
    onLogoutClick: () -> Unit,
    locationViewModel: LocationViewModel
) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("StudyPath" )},
                    colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            bottomBar = { BottomAppBar(navController, "task") }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    text = "Navigation Screen",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )

                PlacesMapScreen(
                    modifier = Modifier.weight(1f),
                    currentLocation = locationViewModel.lastLocation,
                    getLastLocation = { locationViewModel.getLastLocation() },
                    hasLocationPermission = { locationViewModel.hasPermission() },
                )
            }
        }
    }


@Composable
private fun PlacesMapScreen(
    modifier: Modifier = Modifier,
    currentLocation: Location?,
    getLastLocation: () -> Unit = {},
    hasLocationPermission:  () -> Boolean = { false },
    //placesList: List<BurgerPlace> = listOf()
) {
    //use this for the dimensions of Limerick when running in an emulator
    val defaultLocation = LatLng(52.6638, -8.6267)

    val cameraPositionState = rememberCameraPositionState {}

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = true,
                myLocationButtonEnabled = true,
                rotationGesturesEnabled = true,
                scrollGesturesEnabled = true,
                scrollGesturesEnabledDuringRotateOrZoom = true,
                tiltGesturesEnabled = true,
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true
            )
        )
    }

    val properties by remember {
        mutableStateOf(
            MapProperties(
                isBuildingEnabled = false,
                isMyLocationEnabled = hasLocationPermission(),
                isIndoorEnabled = false,
                isTrafficEnabled = false,
                mapType = MapType.NORMAL,
                maxZoomPreference = 21f,
                minZoomPreference = 3f
            )
        )
    }

    GoogleMap(
        modifier = modifier,
        properties = properties,
        uiSettings = uiSettings,
        cameraPositionState = cameraPositionState
    ) {
        getLastLocation()
        val location = currentLocation?.let {
            //replace defaultLocation with currentLocation when running on a physical device
            LatLng(defaultLocation.latitude, defaultLocation.longitude)
        }
//        for (place in placesList) {
//            DisplayMarker(place)
//        }
        location?.let {
            cameraPositionState.move(
                update = CameraUpdateFactory.newLatLngZoom(it, 12f)
            )
        }
    }
}
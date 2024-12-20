package com.example.studypath.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studypath.navigation.BottomAppBar
import com.example.studypath.viewmodel.LocationViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


//My Maps dont work the same way as bugora, where all markers would be displayed automatically
//because I want to allow the user to choose a campus/room I need to have the markers display depending on the users selection
//My idea is to have a list of campus's with hardcoded rooms and coordinates, when the user selects a campus
//and a room, the marker will be places on that rooms coordinates

//UPDATE: theres and issue with my Room Drop down where it loads on "Limerick: Mid-West" for some reason
//Ive tried to pin point why but its not working and I really ament sure
//Minor Ui bug but still a bug
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavigationScreen(
    navController: NavController,
    locationViewModel: LocationViewModel
) {



    //Hardcoded campus's and rooms with coordinates
    //Room key = Room name, Value = Coordinates
    //Map made the most sense to me here because I wanted to allow the user to select a room in a campus and have that automatically assigned
    //If it wasnt hardcoded it would be more then likely a DB call to a Room entity which could be a child of a Campus entity, with a one to many relationship
    //Room then would have name and coordinates which I could just get from a call then if it wasnt hardcoded
    //eg. (GET * FROM Room WHERE Campus = :selectedCampus) gets the room names and coordinates for the selected campus or something along those lines
    val campusRooms = mapOf(
        "Limerick: Mid-West" to mapOf(
            "8A103" to LatLng(52.674815, -8.649234),
            "8B104" to LatLng(52.675271, -8.648788),
            "8C201" to LatLng(52.675108, -8.647864)
        ),
        "Thurles" to mapOf(
            "7B108" to LatLng(52.686487, -7.827040),
            "7A109" to LatLng(52.686448, -7.825751),
            "Canteen" to LatLng(52.685977, -7.826100)
        )
    )
    //Lat, Long for TUS Limerick
    val defaultLocation = LatLng(52.674868, -8.648741)

    var selectedCampus by remember { mutableStateOf(campusRooms.keys.first()) }
    var selectedRoom by remember { mutableStateOf(campusRooms[selectedCampus]?.keys?.first() ?: "") }
    var selectedLocation by remember { mutableStateOf(defaultLocation) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("StudyPath")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondary),
            )
        },
        bottomBar = { BottomAppBar(navController, "navigation") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            PlacesMapScreen(
                modifier = Modifier.weight(1f),
                targetLocation = selectedLocation ?: defaultLocation,
            )

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Campus: ",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                DropDownBox(
                    options = campusRooms.keys.toList(),
                    selectedOption = selectedCampus,
                    onOptionSelected = { newCampus ->//When a user selects a new campus option
                        selectedCampus = newCampus //First update selected campus
                        selectedRoom = campusRooms[newCampus]?.keys?.first() ?: "" //Then update selected room to the first room in the new campus, if null, just leave blank for now (wont be null because its hardcoded but still)
                    }
                )
            }


            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Room: ",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                DropDownBox(
                    options = campusRooms[selectedCampus]?.keys?.toList() ?: emptyList(), //List of rooms depending on selected campus
                    selectedOption = selectedRoom,
                    onOptionSelected = { newRoom ->
                        selectedRoom = newRoom
                    }
                )
            }

            Button(
                onClick = {
                    selectedLocation = campusRooms[selectedCampus]?.get(selectedRoom)!!
                },
                modifier = Modifier.padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(text = "FIND",)
            }

        }
    }
}


@SuppressLint("UnrememberedMutableState")
@Composable
private fun PlacesMapScreen(
    modifier: Modifier = Modifier,
    targetLocation: LatLng,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(targetLocation, 15f) // Set initial camera position
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        Marker(state = MarkerState(position = targetLocation), title = "Selected Room")
    }
}

//Was split between using a ExposedDropdownMenuBox or a Spinner for the dropdown box
//But after looking at both, Geeks for Geeks says that the "Spinner is not that customizable like the new exposed Drop-Down menu."
//so i went with the ExposedDropdownMenuBox
//https://www.geeksforgeeks.org/exposed-drop-down-menu-in-android/
//This is where I figured out how to use the ExposedDropdownMenuBox
//https://composables.com/material3/exposeddropdownmenubox
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownBox(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCampus by remember { mutableStateOf("Limerick: Mid-West") }

    ExposedDropdownMenuBox (
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = selectedCampus,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedCampus = option
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
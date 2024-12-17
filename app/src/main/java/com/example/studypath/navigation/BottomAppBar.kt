package com.example.studypath.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost

@Composable
fun BottomAppBar(navController: NavController, currentRoute: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //TASK PAGE ICON
            IconButton(
                onClick = { navController.navigate("task") }
            ) {
                Icon(
                    if(currentRoute == "task") Icons.Filled.Menu else Icons.Outlined.Menu,
                    contentDescription = "Tasks"
                )
            }

            //CALENDAR PAGE ICON
            IconButton(
                onClick = {  }
            ) {
                Icon(
                    if(currentRoute == "calender") Icons.Filled.DateRange else Icons.Outlined.DateRange,
                    contentDescription = "calender"
                )
            }

            //NAVIGATION PAGE ICON
            IconButton(
                onClick = { navController.navigate("navigation") }
            ) {
                Icon(
                    if(currentRoute == "navigation") Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "navigation"
                )
            }
        }
    }

}
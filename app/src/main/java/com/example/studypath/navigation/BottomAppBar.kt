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
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState

//Just a bottomAppBar I got most of it from Bugora, but set my screens up differently than in Bugora
//so I cant use the foreach, so ive just manually added the icons at the bottom
@Composable
fun BottomAppBar(navController: NavController, currentRoute: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxWidth()
            ) {

                NavigationBarItem(
                    selected = currentRoute == "task",
                    onClick = { navController.navigate("task") },
                    icon = {
                        Icon(
                            if(currentRoute == "task") Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "task"
                        )
                    },
                    label = {
                        Text("Tasks")
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == "calender",
                    onClick = {  },
                    icon = {
                        Icon(
                            if(currentRoute == "calender") Icons.Filled.DateRange else Icons.Outlined.DateRange,
                            contentDescription = "calender"
                        )
                    },
                    label = {
                        Text("Calender")
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == "navigation",
                    onClick = { navController.navigate("navigation") },
                    icon = {
                        Icon(
                            if(currentRoute == "navigation") Icons.Filled.Search else Icons.Outlined.Search,
                            contentDescription = "navigation"
                        )
                    },
                    label = {
                        Text("Navigation")
                    }
                )

            }

        }
    }

}
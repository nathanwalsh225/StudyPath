package com.example.studypath.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithSidebar(
    userEmail: String,
    userName: String,
    onLogoutClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope() //Using a coroutine Scope for side menu
    Log.d("newUserCheck", "USER: $userEmail + $userName")

    ModalNavigationDrawer(
        drawerContent = {
            SidebarContent(
                userEmail = userEmail,
                userName = userName,
                onLogoutClick = onLogoutClick,
                onCloseDrawer = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            )
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("StudyPath") },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open Drawer")
                        }
                    }
                )
            }
        ) {
            content() //What ever screen gets passed into here
        }
    }
}

@Composable
fun SidebarContent(
    userEmail: String?,
    userName: String?,
    onLogoutClick: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        Text(
            text = "Menu",
            style = MaterialTheme.typography.titleLarge.copy( //.copy gets that paticular style but allows me to still edit certain bits eg. color
                color = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.height(24.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Account Name: $userName",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
                text = "Email: $userEmail",
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.weight(1f)) //Push the logout button to the bottom

        Button (
            onClick = {
                onCloseDrawer()
                onLogoutClick()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.error,
                containerColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text( text = "LOGOUT")
        }
    }
}
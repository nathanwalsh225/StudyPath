package com.example.studypath.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.studypath.R
import com.example.studypath.navigation.NavGraph
import com.example.studypath.ui.theme.StudyPathTheme
import com.example.studypath.viewmodel.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private val locationViewModel: LocationViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyPathTheme(dynamicColor = false) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    Scaffold(modifier = Modifier.fillMaxSize()) {
                        GatherPermissions {
                            NavGraph(navController, null)
                        }
                    }
                }

            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun GatherPermissions(content: @Composable () -> Unit) {
        //I compeletly and utterly goosed myself in terms of the permissions, I had not planned correctly for
        //the permissions, I had previously Post Notifications working but I had set that up as too much of a
        //'focus' where it ended up being actually quite difficult to get the permissions working for location
        //I had to go back and rework the permissions to get them to work properly (Its not perfect but it works)
        //Left what I was trying to do below just for clarity on my initial idea
        val permissionState = rememberMultiplePermissionsState(
           permissions = buildList { //This is a list of Map permissions that we need to ask for
               add(Manifest.permission.ACCESS_COARSE_LOCATION)
               add(Manifest.permission.ACCESS_FINE_LOCATION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.POST_NOTIFICATIONS) //if the current SDK version is greater than TIRAMISU (33) then we need to ask for this permission
                }
           }
        )
        locationViewModel.updatePermissionState(permissionState)

        //On app start, ask for permissions
        LaunchedEffect(true) { //make sure it only runs once
            permissionState.launchMultiplePermissionRequest()
        }

        if(permissionState.allPermissionsGranted) {
            initializeApp()
            content()
        } else {
            //If permissions are not granted, show a message to the user
            //I dont know what to do here (nothing really) because we need the permissions
        }
    }









    private fun initializeApp() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token) //This is all for myself for getting the message stuff atm
            Log.d("TAG", msg)
           // Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        }

//        setContent {
//            StudyPathTheme(dynamicColor = false) {
//                Surface(color = MaterialTheme.colorScheme.background) {
//                    val navController = rememberNavController()
//                    NavGraph(navController, requestPermissionLauncher)
//                }
//            }
//        }
    }

//    //https://firebase.google.com/docs/cloud-messaging/android/client
//    //Everything Ive gotten from the firebase documentation
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission(),
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            // Permission is granted reinitialize the app
//        } else {
//            // Permission is denied
//            //Not really sure what to do here or if I even need to do anything because we need the messages
//        }
//    }
//
//    private fun askNotificationPermission() {
//        // This is only necessary for API level >= 33 (TIRAMISU)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//                PackageManager.PERMISSION_GRANTED
//            ) { //User has already granted permission
//                initializeApp() //initialize the app
//
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                //got this from the android developer documentation i dont know if ill use it yet
//                // TODO: display an educational UI explaining to the user the features that will be enabled
//            } else {
//                //Ask User for permission
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        } else { //anything under android 33 (TIRAMISU) automatically has notications enabled so jsut let them in
//            initializeApp()
//        }
//    }
//
//


}


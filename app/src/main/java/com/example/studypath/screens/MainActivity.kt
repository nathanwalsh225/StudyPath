package com.example.studypath.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.studypath.R
import com.example.studypath.navigation.NavGraph
import com.example.studypath.ui.theme.StudyPathTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
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

        setContent {
            StudyPathTheme(dynamicColor = false) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavGraph(navController, requestPermissionLauncher)
                }
            }
        }
    }

    //https://firebase.google.com/docs/cloud-messaging/android/client
    //Everything Ive gotten from the firebase documentation
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted reinitialize the app
        } else {
            // Permission is denied
            //Not really sure what to do here or if I even need to do anything because we need the messages
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) { //User has already granted permission
                initializeApp() //initialize the app

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                //got this from the android developer documentation i dont know if ill use it yet
                // TODO: display an educational UI explaining to the user the features that will be enabled
            } else {
                //Ask User for permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else { //anything under android 33 (TIRAMISU) automatically has notications enabled so jsut let them in
            initializeApp()
        }
    }




}


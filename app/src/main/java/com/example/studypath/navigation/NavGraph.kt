package com.example.studypath.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studypath.database.DatabaseProvider
import com.example.studypath.screens.LoginScreen
import com.example.studypath.screens.RegisterScreen
import com.example.studypath.screens.TaskScreen
import com.example.studypath.viewmodel.AuthViewModel
import com.example.studypath.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login",
    ) {
        val authViewModel = AuthViewModel()


        composable("login") {
            //clear the db just incase
//            val database = DatabaseProvider.getDatabase(LocalContext.current)
//            database.clearAllTables()

//            LocalContext.current.deleteDatabase("study-path-db")


            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("task") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {

                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("task") {
                        popUpTo("register") {
                            inclusive = true
                        }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("task") {
            val user = FirebaseAuth.getInstance().currentUser //If user is not logged in dont allow access TODO is this essential?
            if (user == null) {
                navController.navigate("login") {
                    popUpTo("task") { inclusive = true }
                }
            }

            val taskDao = DatabaseProvider.getDatabase(context = LocalContext.current).taskDao()
            val viewModel = TaskViewModel(taskDao)
            val userEmail = user?.email ?: "No Email"
            val userName = user?.displayName ?: "Unknown User"

            TaskScreen(
                viewModel = viewModel,
                onAddTaskClick = { },
                userName = userName,
                userEmail = userEmail,
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("task") { inclusive = true }
                    }
                },
                onEditTaskClick = { }
            )
        }
    }
}
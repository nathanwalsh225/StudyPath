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
import com.example.studypath.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(navController: NavHostController) {
    //TODO Optimize this as I can see it getting messy
    NavHost(
        navController = navController,
        startDestination = "login",
    ) {
        composable("login") {
            //can be used to clear the db just incase
//            val database = DatabaseProvider.getDatabase(LocalContext.current)
//            database.clearAllTables()

//            LocalContext.current.deleteDatabase("study-path-db")

            val userDao = DatabaseProvider.getDatabase(LocalContext.current).userDao()
            LoginScreen(
                authViewModel = AuthViewModel(userDao),
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
            val userDao = DatabaseProvider.getDatabase(LocalContext.current).userDao()
            RegisterScreen(
                authViewModel = AuthViewModel(userDao),
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

            Log.d("TaskScreen", "User: $user")
            val userEmail = user?.email ?: "No Email"
            val userName = user?.displayName ?: "Unknown User"

            TaskScreen(
                taskViewModel = TaskViewModel(
                    taskDao = DatabaseProvider.getDatabase(context = LocalContext.current).taskDao(),
                    subtaskDao = DatabaseProvider.getDatabase(context = LocalContext.current).subtaskDao()
                ),
                userViewModel = UserViewModel(
                    userDao = DatabaseProvider.getDatabase(context = LocalContext.current).userDao(),
                    taskDao = DatabaseProvider.getDatabase(context = LocalContext.current).taskDao()
                ),
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
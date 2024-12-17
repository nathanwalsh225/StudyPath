package com.example.studypath.navigation

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.studypath.database.DatabaseProvider
import com.example.studypath.model.Task
import com.example.studypath.screens.AddOrUpdateTaskScreen
import com.example.studypath.screens.LoginScreen
import com.example.studypath.screens.NavigationScreen
import com.example.studypath.screens.RegisterScreen
import com.example.studypath.screens.TaskScreen
import com.example.studypath.viewmodel.AuthViewModel
import com.example.studypath.viewmodel.LocationViewModel
import com.example.studypath.viewmodel.TaskViewModel
import com.example.studypath.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(
    navController: NavHostController,
    activityResultLauncher :ActivityResultLauncher<String>?,
    locationViewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current
    val database = DatabaseProvider.getDatabase(context)

    val userViewModel = remember {
        UserViewModel(
            userDao = database.userDao(),
            taskDao = database.taskDao()
        )
    }

    val taskViewModel = remember {
        TaskViewModel(
            taskDao = database.taskDao(),
            subtaskDao = database.subtaskDao(),
            userViewModel = userViewModel
        )
    }


    //TODO Optimize this as I can see it getting messy
    NavHost(
        navController = navController,
        startDestination = "login",
    ) {
        composable("login") {

            val userDao = DatabaseProvider.getDatabase(LocalContext.current).userDao()
            LoginScreen(
                authViewModel = AuthViewModel(userDao, context),
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
                authViewModel = AuthViewModel(userDao, context),
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
            val user =
                FirebaseAuth.getInstance().currentUser //If user is not logged in dont allow access TODO is this essential?
            if (user == null) {
                navController.navigate("login") {
                    popUpTo("task") { inclusive = true }
                }
            }

            //activityResultLauncher.launch("Permission")

            val userEmail = user?.email ?: "No Email"
            val userName = user?.displayName ?: "Unknown User"

            TaskScreen(
                taskViewModel = taskViewModel,
                userViewModel = userViewModel,
                onAddTaskClick = { navController.navigate("addTask") },
                onEditTaskClick = { task ->
                    navController.navigate("updateTask/${task.taskId}") {
                        popUpTo("updateTask/${task.taskId}") { inclusive = true }
                    }
                },
                userName = userName,
                userEmail = userEmail,
                navController = navController,
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("task") { inclusive = true }
                    }
                }
            )
        }

        composable("addTask") {
            AddOrUpdateTaskScreen(
                userViewModel = userViewModel,
                taskViewModel = taskViewModel,
                onTaskAdded = {
                    navController.popBackStack()
                },
                onBackClicked = { navController.popBackStack() }
            )

        }

        composable("updateTask/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()

            if (taskId == null) {
                navController.popBackStack()
            } else {
                val taskState = remember { mutableStateOf<Task?>(null) }

                LaunchedEffect(taskId) {
                    taskState.value = taskViewModel.getTask(taskId)
                }
                if (taskState.value == null) {
                    // Show loading or fallback UI
                    Text("Loading...")
                } else {
                    AddOrUpdateTaskScreen(
                        userViewModel = userViewModel,
                        taskViewModel = taskViewModel,
                        onTaskAdded = {
                            navController.popBackStack()
                        },
                        onBackClicked = { navController.popBackStack() },
                        existingTask = taskState.value
                    )
                }
            }
        }

        composable("calender") {
            Text("Calender")
        }

        composable("navigation") {
            NavigationScreen(
                navController = navController,
                userEmail = "email",
                userName = "name",
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("navigation") { inclusive = true }
                    }
                },
                locationViewModel = locationViewModel
            )
        }
    }
}
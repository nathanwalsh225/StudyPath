package com.example.studypath.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.studypath.model.Task
import com.example.studypath.model.User
import com.example.studypath.repository.TaskDao
import com.example.studypath.repository.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserViewModel(private val userDao: UserDao, private val taskDao: TaskDao) : ViewModel() {

    val user = mutableStateOf<User?>(null)
    val tasks = mutableStateOf<List<Task>>(emptyList())

    //https://chatgpt.com/share/67532216-8ea4-8004-9952-d044ec0342e5
    //Had alot of issues with this, was initially trying to force recomposition of the UI but it wasnt working
    //gave up after a few hours and was recommended to use a state instead and return the actual data using the function
    //so I asked chat how to return the two values and it suggested using a Pair (Like a Map in Java), as i wasnt sure how to do it in Kotlin
    suspend fun fetchUserAndTasks(email: String): Pair<User?, List<Task>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("TaskScreen", "Fetching user and tasks for $email")
                val fetchedUser = userDao.getUserByEmail(email.trim())
                val fetchedTasks = fetchedUser?.let {
                    taskDao.getAllTasksForUser(it.userId)
                } ?: emptyList()

                user.value = fetchedUser
                tasks.value = fetchedTasks

                Pair(fetchedUser, fetchedTasks)
            } catch (e: Exception) {
                Log.d("UserViewModel", "Failed to fetch user - ${e.message}")
                Pair(null, emptyList())
            }
        }
    }
}
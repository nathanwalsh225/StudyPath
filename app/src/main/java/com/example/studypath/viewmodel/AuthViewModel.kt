package com.example.studypath.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studypath.database.DatabaseProvider
import com.example.studypath.model.User
import com.example.studypath.repository.UserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(private val userDao: UserDao, context: Context) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

        //code to delete db if needed
//    init {
//        DatabaseProvider.getDatabase(context, true)
//    }


    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Login Failed")
                }
            }
    }

    fun register(firstName: String, lastName: String, email: String, password: String, onRegisterSuccess: () -> Unit, onError: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        Log.d("TaskScreen", "Got in")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TaskScreen", "Registering user IN")
                    //Getting the users name just to save it here so I can use it in the app
                    val user = auth.currentUser
                    Log.d("TaskScreen", "Current User $user")
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName("$firstName $lastName")
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                onRegisterSuccess()
                                viewModelScope.launch(Dispatchers.IO) {
                                    val id = userDao.insertUser(User(0, email, password, firstName, lastName)) //Inserting user to DB
                                    Log.d("TaskScreen", "UserId $id ")

                                    if (id > 0) {
                                        println("User inserted successfully")
                                    } else {
                                        onError("Profile update failed: ${profileTask.exception?.message}")
                                    }
                                }
                            } else {
                                // Error for if the profile was unable to update
                                println("Profile update error: ${profileTask.exception?.message}")
                            }
                        }
                } else {
                    // Error for any issues in registration
                    println("Registration error: ${task.exception?.message}")
                }
            }


        fun logout() {
            auth.signOut()
        }
    }
}
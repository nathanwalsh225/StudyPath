package com.example.studypath.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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

    fun register(firstName: String, lastName: String, email: String, password: String, onRegisterSuccess: () -> Unit) {
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Getting the users name just to save it here so I can use it in the app
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName("$firstName $lastName")
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                onRegisterSuccess()
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
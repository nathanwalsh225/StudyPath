package com.example.studypath.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true)
    val userId: Int,
    val email: String,
    val firstName: String,
    val lastName: String

)
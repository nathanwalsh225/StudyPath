package com.example.studypath.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studypath.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) //Can be used as an update now with this line
    fun insertUser(user: User) : Long

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUser(userId: Int): User?

    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmail(email: String): User?

}
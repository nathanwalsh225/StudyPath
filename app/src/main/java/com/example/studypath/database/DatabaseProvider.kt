package com.example.studypath.database

import android.content.Context
import androidx.room.Room

//Singleton class for providing the database
//https://developer.android.com/reference/android/arch/persistence/room/RoomDatabase.Builder
object DatabaseProvider {
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        if (INSTANCE == null) {
            //create the db using Room.databaseBuilder
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "study-path-db"
            ).fallbackToDestructiveMigration()
                .build()
        }
        return INSTANCE!!
    }
}
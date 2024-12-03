package com.example.studypath.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studypath.model.Task
import com.example.studypath.repository.TaskDao

@Database(entities = [Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao



}
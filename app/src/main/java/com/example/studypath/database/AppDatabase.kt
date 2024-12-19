package com.example.studypath.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studypath.config.SubtaskConverter
import com.example.studypath.model.Subtasks
import com.example.studypath.model.Task
import com.example.studypath.model.User
import com.example.studypath.repository.SubtaskDao
import com.example.studypath.repository.TaskDao
import com.example.studypath.repository.UserDao

@Database(entities = [Task::class, Subtasks::class, User::class], version = 6)
@TypeConverters(SubtaskConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun subtaskDao(): SubtaskDao


}
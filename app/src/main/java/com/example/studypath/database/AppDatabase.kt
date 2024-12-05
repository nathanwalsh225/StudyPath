package com.example.studypath.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studypath.config.SubtaskConverter
import com.example.studypath.model.Subtasks
import com.example.studypath.model.Task
import com.example.studypath.repository.TaskDao

@Database(entities = [Task::class, Subtasks::class], version = 2)
@TypeConverters(SubtaskConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao



}
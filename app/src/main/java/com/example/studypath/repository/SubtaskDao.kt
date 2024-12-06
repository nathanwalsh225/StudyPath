package com.example.studypath.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.studypath.model.Subtasks

@Dao
interface SubtaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubtasks(subtask: Subtasks): Long
}
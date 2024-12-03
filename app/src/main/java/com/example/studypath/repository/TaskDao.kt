package com.example.studypath.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.studypath.model.Task

//Task Repository
@Dao
interface TaskDao {
    @Insert
    fun insertTask(task: Task)

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("DELETE FROM tasks WHERE id = :taskId")
    fun deleteTask(taskId : Int)
}

package com.example.studypath.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studypath.model.Subtasks
import com.example.studypath.model.Task
import com.example.studypath.model.User

//Task Repository
@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY priority ASC")
    fun getAllTasksForUser(userId: Int): List<Task>

    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    fun deleteTask(taskId : Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: Task) : Long

    @Query("UPDATE tasks SET subtasks = :subtasks WHERE taskId = :taskId")
    fun updateSubtasks(taskId: Int, subtasks: List<Subtasks>)

    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    fun getTask(taskId: Int): Task?

    @Update
    fun updateTask(task: Task)
}

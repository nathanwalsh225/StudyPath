package com.example.studypath.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studypath.model.Subtasks
import com.example.studypath.model.Task
import com.example.studypath.repository.SubtaskDao
import com.example.studypath.repository.TaskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao, private val subtaskDao: SubtaskDao) : ViewModel() {

    fun addTask(task: Task) {
        //https://chatgpt.com/share/6751d453-4a88-8004-a975-22ce544b3a7b
        viewModelScope.launch(Dispatchers.IO) { //making the operation run on a separate thread to prevent crashes
            try {
                val taskId = taskDao.insertTask(task).toInt() //inserting the task into the database
                val updatedSubtasks = mutableListOf<Subtasks>()

                task.subtasks.forEach { subtask -> //for each subtask, assign the id of the task to the subtask so it can be fetched propolly
                    val subtaskId = subtaskDao.insertSubtasks(subtask.copy(taskId = taskId)).toInt()
                    updatedSubtasks.add(subtask.copy(id = subtaskId))
                }

                val updatedTask = task.copy(subtasks = updatedSubtasks)
                Log.d("TaskScreen", "Task added: $updatedTask")
                taskDao.updateSubtasks(taskId, updatedSubtasks)

                //TODO TEMPROARY FIX, need to fix the subtask insertion
                //If you are reading this, please dont - I clearly forgot / didnt fix this
                //I need to be able to ensure each subtask is assigned the correct correlating task id, however I cannot get the taskId
                //Unit I actually create the task in the DB, unless I manually assign it here in the code, which I will not do for obvious reasons
                //As a result my temporary fix is to just insert the subtasks with the task id as 0, which is not ideal but it works for now
                //and then get the id of the task created, and update the subtasks with the correct task id then reupdate the task
                //Without the second insert (which just acts as an update) the subtasks will not be assigned the correct task id in the DB
                //taskDao.insertTask(createdTask)

                Log.d("TaskViewModel", "Task added: $taskId")
            } catch (e: Exception) {
                Log.d("TaskViewModel", "Task not added: $task - ${e.message}")
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.deleteTask(taskId)
        }
    }

}
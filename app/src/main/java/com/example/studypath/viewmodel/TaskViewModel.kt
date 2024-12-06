package com.example.studypath.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                val taskId = taskDao.insertTask(task).toInt()
                task.subtasks.forEach {
                    subtaskDao.insertSubtasks(it.copy(taskId = taskId))
                }
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
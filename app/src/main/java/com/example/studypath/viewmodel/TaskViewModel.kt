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
import kotlinx.coroutines.withContext

class TaskViewModel(
    private val taskDao: TaskDao,
    private val subtaskDao: SubtaskDao,
    private val userViewModel: UserViewModel
) : ViewModel() {

    fun addTask(task: Task, email: String) {
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

                 userViewModel.fetchUserAndTasks(email)
                Log.d("TaskViewModel", "Task added: $taskId")
            } catch (e: Exception) {
                Log.d("TaskViewModel", "Task not added: $task - ${e.message}")
            }
        }
    }

     fun deleteTask(taskId: Int, onRefresh: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskDao.deleteTask(taskId)
                withContext(Dispatchers.Main) {
                    onRefresh()
                }
            } catch (e: Exception) {
                Log.d("TaskViewModel", "Error -${e.message}")
            }
        }
    }

}
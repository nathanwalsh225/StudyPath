package com.example.studypath.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.studypath.model.Task
import com.example.studypath.repository.TaskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {

    //private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = taskDao.getAllTasks()

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskDao.insertTask(task)
        }
    }

//    fun loadTasks() {
//        viewModelScope.launch {
//            tasks.postValue(taskDao.getAllTasks())
//        }
//    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            taskDao.deleteTask(taskId)
        }
    }
}
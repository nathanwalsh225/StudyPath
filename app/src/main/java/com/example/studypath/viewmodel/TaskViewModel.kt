package com.example.studypath.viewmodel

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.studypath.config.NotificationScheduler
import com.example.studypath.model.Subtasks
import com.example.studypath.model.Task
import com.example.studypath.repository.SubtaskDao
import com.example.studypath.repository.TaskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.text.format

class TaskViewModel(
    private val taskDao: TaskDao,
    private val subtaskDao: SubtaskDao,
    private val userViewModel: UserViewModel
) : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
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

                val result = scheduleTaskNotification(task, 0)
                Log.d("TAG", "Task scheduled: $result")

                userViewModel.fetchUserAndTasks(email)
                Log.d("TaskViewModel", "Task added: $taskId")
            } catch (e: Exception) {
                Log.d("TaskViewModel", "Task not added: $task - ${e.message}")
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("TaskViewModel", "Task updated: $task")
                taskDao.updateTask(task)

                val updatedSubtasks = mutableListOf<Subtasks>()

                task.subtasks.forEach { subtask ->
                    if (subtask.id > 0) {
                        updatedSubtasks.add(subtask)
                        taskDao.updateSubtasks(task.taskId, updatedSubtasks)
                    } else {
                        val subtaskId =
                            subtaskDao.insertSubtasks(subtask.copy(taskId = task.taskId)).toInt()
                        updatedSubtasks.add(subtask.copy(id = subtaskId))
                        taskDao.updateSubtasks(task.taskId, updatedSubtasks)
                    }
                }


            } catch (e: Exception) {
                Log.d("TaskViewModel", "Task not updated: $task - ${e.message}")
            }
        }
    }

    suspend fun getTask(taskId: Int): Task {
        return withContext(Dispatchers.IO) {
            try {
                val task = taskDao.getTask(taskId)
                Log.d("TaskViewModel", "Task fetched: $task")
                task!!
            } catch (e: Exception) {
                Log.d("TaskViewModel", "Task not fetched: $taskId - ${e.message}")
                Task(0, 0, "", "", 0, emptyList())
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

    private fun scheduleTaskNotification(
        task: Task,
        msgDelay: Long
    ) { //TODO IMPLEMENT FOR IF AN UPDATE OCCURS
        Log.d("TAG", "Task scheduled: $msgDelay")
        val inputData = Data.Builder()
            .putString("taskName", task.name)
            .putString("subtasks", task.subtasks.size.toString())
            .build()

        Log.d("TAG", "Task scheduled: $inputData")

        val workRequest = OneTimeWorkRequestBuilder<NotificationScheduler>()
            .setInputData(inputData)
            .setInitialDelay(msgDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance().enqueue(workRequest)
    }


    //I had initially wanted to create a system where task reminders would be sent based on a few different factors e.g Priority, Due Date, Number of subtasks left
    //But in order to have FCM messages working outside and indepently of the app (in order to schedule notifications), I would need to have a server to send the messages
    //Aswell as some FireStore functions which Im pretty sure costs money
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTaskDelayTime(task: Task): Long {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val msgDue = format.parse(task.dueDate)

        msgDue.time -= TimeUnit.DAYS.toDays(task.subtasks.size.toLong())
        //msgDue.time -= TimeUnit.DAYS.toMinutes(1)
        return TimeUnit.SECONDS.toMillis(10)
        //return msgDue.time
    }
}
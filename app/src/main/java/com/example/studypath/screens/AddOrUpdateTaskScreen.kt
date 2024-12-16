package com.example.studypath.screens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.studypath.model.Subtasks
import com.example.studypath.model.Task
import com.example.studypath.viewmodel.TaskViewModel
import com.example.studypath.viewmodel.UserViewModel

@Composable
fun AddOrUpdateTaskScreen(
    userViewModel: UserViewModel?,
    taskViewModel: TaskViewModel?,
    onTaskAdded: () -> Unit,
    onBackClicked: () -> Unit,
    existingTask: Task? = null
) {
    var taskName by remember { mutableStateOf(existingTask?.name ?: "") }
    var dueDate by remember { mutableStateOf(existingTask?.dueDate ?: "") }
    var priority by remember { mutableStateOf(existingTask?.priority ?: 1) }
    var subtasks by remember { mutableStateOf(existingTask?.subtasks?.map { it.name }.orEmpty()) }
    val focusManager = LocalFocusManager.current

    //https://chatgpt.com/share/67587500-55fc-8004-af78-0ef35080c015
    //got datepicker code from gpt
    val calendar = java.util.Calendar.getInstance()
    val year = calendar.get(java.util.Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH)
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            dueDate = "$selectedDay/${selectedMonth + 1}/$selectedYear" // Update the due date
        },
        year, month, day
    )

    // Set minimum date to today's date
    datePickerDialog.datePicker.minDate = calendar.timeInMillis


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = if (existingTask == null) "Add Task" else "Edit Task",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }

            item {
                TextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(1 to "Low", 2 to "Medium", 3 to "High").forEach { (value, label) ->
                        Button(
                            onClick = { priority = value },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (priority == value)
                                    MaterialTheme.colorScheme.secondary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                        ) {
                            Text(label, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }


            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() } //show the datepicker initiallized above on click
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Due Date",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = dueDate.ifEmpty { "Select Due Date" },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.padding(16.dp))
            }


            item {
                Text(
                    text = "Sub-Tasks:",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }


                    items(subtasks.size) { index ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = subtasks[index],
                                onValueChange = { newValue ->
                                    subtasks =
                                        subtasks.toMutableList().apply { set(index, newValue) }
                                },
                                label = { Text("Subtask ${index + 1}") },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                subtasks = subtasks.toMutableList().apply { removeAt(index) }
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Subtask"
                                )
                            }
                        }
                    }

            item {
                Button(
                    onClick = { subtasks = subtasks + "" },
                    //subtasks = subtasks.toMutableList().apply { add("") }
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Add Sub-Task")
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        onClick = {
                            //Create the task and submit it
                            if(existingTask == null) { //code for adding task
                                Log.d("TaskViewModel", "Entered Add")
                                if (taskName.isNotEmpty() && dueDate.isNotEmpty()) {
                                    val task = Task(
                                        name = taskName,
                                        dueDate = dueDate,
                                        priority = priority,
                                        userId = userViewModel!!.user.value?.userId ?: 0,
                                        subtasks = subtasks.map { name ->
                                            Subtasks(
                                                name = name,
                                                completed = false,
                                                taskId = 0
                                            )
                                        }
                                    )

                                    userViewModel.user.value?.let {
                                        taskViewModel!!.addTask(
                                            task,
                                            it.email
                                        )
                                    }
                                    onTaskAdded()
                                }
                            } else { //code for updating task
                                Log.d("TaskViewModel", "Entered Edit")
                                if (taskName.isNotEmpty() && dueDate.isNotEmpty()) {
                                    val task = Task(
                                        taskId = existingTask.taskId,
                                        name = taskName,
                                        dueDate = dueDate,
                                        priority = priority,
                                        userId = userViewModel!!.user.value?.userId ?: 0,
                                        subtasks = subtasks.map { name ->
                                            Subtasks(
                                                name = name,
                                                completed = false, //TODO Might set all subtasks to uncomplete if edited, test
                                                taskId = existingTask.taskId
                                            )
                                        }
                                    )

                                    userViewModel.user.value?.let {
                                        taskViewModel!!.updateTask(task)
                                    }
                                    onTaskAdded()
                                }
                            }
                        }) {
                        if (existingTask == null) {
                            Text("ADD TASK")
                        } else {
                            Text("UPDATE TASK")
                        }
                    }
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        onClick = { onBackClicked() }
                    ) {
                        Text("BACK")
                    }
                }
            }
        }
    }
}

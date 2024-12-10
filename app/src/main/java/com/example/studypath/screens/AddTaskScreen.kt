package com.example.studypath.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studypath.model.Subtasks
import com.example.studypath.model.Task
import com.example.studypath.model.User
import com.example.studypath.viewmodel.TaskViewModel
import com.example.studypath.viewmodel.UserViewModel

@Composable
fun AddTaskScreen(
    userViewModel: UserViewModel,
    taskViewModel: TaskViewModel,
    onTaskAdded: () -> Unit,
    onBackClicked: () -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(1) }
    var subtasks by remember { mutableStateOf(listOf<String>()) }
    val focusManager = LocalFocusManager.current

    //https://chatgpt.com/share/67587500-55fc-8004-af78-0ef35080c015
    //got datepicker code from gpt
    val calendar = java.util.Calendar.getInstance()
    val year = calendar.get(java.util.Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH)
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

    val context = LocalContext.current
    val datePickerDialog = android.app.DatePickerDialog(
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
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Add Task",
                style = MaterialTheme.typography.headlineMedium
            )

            TextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Name") },
                modifier = Modifier.fillMaxWidth()
            )


            Text(
                text = "Priority",
                style = MaterialTheme.typography.bodyMedium
            )

            listOf(1, 2, 3).forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { priority = option }
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = priority == option,
                        onClick = { priority = option },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (option) {
                            1 -> "Low"
                            2 -> "Medium"
                            3 -> "High"
                            else -> ""
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }


            }

            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() } //show the datepicker initiallized above on click
            ) {
                Text(
                    text = "Due Date",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = dueDate.ifEmpty { "Select Due Date" },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text("Sub-Tasks:", style = MaterialTheme.typography.bodyLarge)
            subtasks.forEachIndexed { index, subtask ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = subtask,
                        onValueChange = { newValue -> //bit of an akaward work around I think but maybe its okay, was getting crashes doing it the with mutableListOf for the subtaks initially so im just updating the list entirely
                            subtasks = subtasks.toMutableList().apply { set(index, newValue) }
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Sub-Task ${index + 1}") }
                    )
                    IconButton(onClick = {
                        subtasks = subtasks.toMutableList().apply { removeAt(index) }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Sub-Task")
                    }
                }

            }

            Button(onClick = {
                subtasks = subtasks.toMutableList().apply { add("") }
            }) {
                Text("Add Sub-Task")
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    //Create the task and submit it
                    Log.d("AddTaskScreen", "Adding task")
                    val task = Task(
                        name = taskName,
                        dueDate = dueDate,
                        priority = priority,
                        userId = userViewModel.user.value?.userId ?: 0,
                        subtasks = subtasks.mapIndexed { index, name ->
                            Subtasks(
                                name = name,
                                completed = false,
                                taskId = 0
                            )
                        }
                    )
                    taskViewModel.addTask(task)
                    onTaskAdded()
                }) {
                    Text("ADD TASK")
                }
                Button(onClick = { onBackClicked() }) {
                    Text("BACK")
                }
            }
        }
    }
}

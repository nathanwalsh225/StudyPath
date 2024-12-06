package com.example.studypath.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studypath.model.Subtasks
import com.example.studypath.model.Task
import com.example.studypath.model.User
import com.example.studypath.navigation.MainScreenWithSidebar
import com.example.studypath.viewmodel.TaskViewModel
import com.example.studypath.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskScreen(
    taskViewModel: TaskViewModel,
    userViewModel: UserViewModel,
    onAddTaskClick: () -> Unit,
    userName: String,
    userEmail: String,
    onEditTaskClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var fetchedData by remember { mutableStateOf<Pair<User?, List<Task>>?>(null) }
    LaunchedEffect(userEmail) {
        fetchedData = userViewModel.fetchUserAndTasks(userEmail)
    }

    //If data has not yet been fetched, just give a loading message
    //TODO maybe implement loading Icon
    if (fetchedData == null) {
        Text("Loading...")
    } else {

        MainScreenWithSidebar(
            userEmail = userEmail,
            userName = userName,
            onLogoutClick = { onLogoutClick() }
        ) {
            Scaffold(
                floatingActionButton = {
                    if (fetchedData?.first != null) {
                        FloatingActionButton(
                            onClick = {
                                Log.d("TaskScreen", "User ID: ${fetchedData!!.first?.userId}")


                                val testTask = Task(
                                    name = "Test Task",
                                    description = "This is a test task.",
                                    dueDate = "2024-12-15",
                                    priority = 2, // Medium priority
                                    userId = fetchedData!!.first?.userId ?: 0,
                                    subtasks = listOf(
                                        Subtasks(name = "Subtask 1", completed = false, taskId = 1),
                                        Subtasks(name = "Subtask 2", completed = true, taskId = 1),
                                    )
                                )


                                taskViewModel.addTask(testTask)
                            }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Task"
                            )
                        }
                    }
                },

                bottomBar = {
                    BottomNavigationBar()
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxWidth()
                ) {
                    items(fetchedData!!.second) { task ->
                        TaskCard(task, onEditTaskClick)
                    }
                }
            }
        }
    }
}


@Composable
fun TaskCard(task: Task, onEditTaskClick: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //TASK TITLE AND EXPAND/COLLAPSE BUTTON
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(task.name, style = MaterialTheme.typography.headlineMedium)
                    Text("Due: ${task.dueDate}", style = MaterialTheme.typography.bodySmall)
                    Text(
                        "Tasks Remaining: ${task.subtasks.size}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    //Text("Priority: ${task.priority}", style = MaterialTheme.typography.bodySmall)
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand/Collapse"
                    )
                }
            }
        }
        //Subtasks Collapsable
        AnimatedVisibility(visible = isExpanded) {
            LazyColumn {
                items(task.subtasks) { subtask ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = subtask.completed,
                            onCheckedChange = { },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Text(subtask.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Button(
            onClick = { onEditTaskClick() },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
        ) {
            Text("Edit")
        }
    }


}

@Composable
fun BottomNavigationBar() {
    BottomAppBar {
        IconButton(onClick = {  }) {
            Icon(Icons.Default.List, contentDescription = "Tasks")
        }
        IconButton(onClick = {  }) {
            Icon(Icons.Default.DateRange, contentDescription = "Calendar")
        }
        IconButton(onClick = {  }) {
            Icon(Icons.Default.Home, contentDescription = "Navigation")
        }
    }
}
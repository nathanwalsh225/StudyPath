package com.example.studypath.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHost
import com.example.studypath.model.Task
import com.example.studypath.model.User
import com.example.studypath.navigation.BottomAppBar
import com.example.studypath.navigation.MainScreenWithSidebar
import com.example.studypath.viewmodel.TaskViewModel
import com.example.studypath.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskScreen(
    taskViewModel: TaskViewModel,
    userViewModel: UserViewModel, //TODO remove ? after testing
    onAddTaskClick: () -> Unit,
    onEditTaskClick: (Task) -> Unit,
    userName: String,
    userEmail: String,
    navController: NavController,
    onLogoutClick: () -> Unit,
    onContactUsClick: () -> Unit
) {
    var fetchedData by remember { mutableStateOf<Pair<User?, List<Task>>?>(null) }

    LaunchedEffect(userEmail) {
        fetchedData = userViewModel.fetchUserAndTasks(userEmail)
    }

    //If data has not yet been fetched, just give a loading message


    //Log.d("TaskScreen", "User ID: ${fetchedData!!.first}")

    MainScreenWithSidebar(
        userEmail = userEmail,
        userName = userName,
        onLogoutClick = { onLogoutClick() },
        onContactUsClick = { onContactUsClick() },
    ) {
        Scaffold(
            floatingActionButton = {
                if (fetchedData?.first != null) {
                    FloatingActionButton(
                        onClick = { onAddTaskClick() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task")
                    }
                }
            },

            bottomBar = { BottomAppBar(navController, "task") }

        ) { paddingValues ->
            //TODO maybe implement loading Icon
            if (fetchedData == null) { //If data has not yet been fetched, just give a loading message
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Loading...", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 64.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(fetchedData!!.second) { task ->
                        TaskCard(
                            task,
                            onEditTaskClick = { task ->
                                onEditTaskClick(task)
                            },
                            onDeleteTaskClick = { taskId ->
                                taskViewModel.deleteTask(taskId) {
                                    taskViewModel.viewModelScope.launch {
                                        fetchedData = userViewModel.fetchUserAndTasks(userEmail)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TaskCard(task: Task, onEditTaskClick: (Task) -> Unit, onDeleteTaskClick: (Int) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //TASK TITLE AND EXPAND/COLLAPSE BUTTON
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(task.name, style = MaterialTheme.typography.headlineLarge)
                    Text("Due: ${task.dueDate}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Tasks Remaining: ${task.subtasks.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    //Text("Priority: ${task.priority}", style = MaterialTheme.typography.bodySmall)
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }
        }
        //Subtasks Collapsable
        AnimatedVisibility(visible = isExpanded) {
            Box(
                modifier = Modifier
                    .heightIn(max = 200.dp) // Limit the height of expanded subtasks to prevent infinite expansion error
            ) {
                if (task.subtasks.isEmpty()) {
                    Text(
                        text = "Keep up the good work!",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(task.subtasks) { subtask ->
                            Log.d("TaskScreen", "Subtask indivs: $subtask")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = subtask.completed,
                                    //having to copy and reapply the subtask list for completing one because it wouldnt recompose previously
                                    //as room does not support mutableLists
                                    onCheckedChange = { isChecked ->
                                        task.subtasks = task.subtasks.map { currentSubtask ->
                                            if (currentSubtask.id == subtask.id) {
                                                currentSubtask.copy(completed = isChecked)
                                            } else {
                                                currentSubtask
                                            }
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                                Text(subtask.name, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    Button(
                        onClick = { onEditTaskClick(task) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
                    ) {
                        Text("Edit")
                    }

                    Button(
                        onClick = { onDeleteTaskClick(task.taskId) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

//@Composable
//fun BottomNavigationBar() {
//    BottomAppBar {
//        IconButton(onClick = { }) {
//            Icon(Icons.Default.List, contentDescription = "Tasks")
//        }
//        IconButton(onClick = { }) {
//            Icon(Icons.Default.DateRange, contentDescription = "Calendar")
//        }
//        IconButton(onClick = { }) {
//            Icon(Icons.Default.Home, contentDescription = "Navigation")
//        }
//    }
//}

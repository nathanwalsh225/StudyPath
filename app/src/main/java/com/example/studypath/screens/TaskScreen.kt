package com.example.studypath.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
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
    userViewModel: UserViewModel,
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

    MainScreenWithSidebar(
        userEmail = userEmail,
        userName = userName,
        onLogoutClick = { onLogoutClick() },
        onContactUsClick = { onContactUsClick() },
        navController = navController
    ) {

        //I was originally going to have the add task button in the nav bar on top but I prefer the look
        //of the floating action button so Im going to keep that
        Scaffold(
            floatingActionButton = {
                if (fetchedData?.first != null) {
                    FloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        onClick = { onAddTaskClick() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task")
                    }
                }
            },
        ) {
            if (fetchedData == null) { //If data has not yet been fetched, just give a loading message
                Box (
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(25.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            } else {
                if (fetchedData!!.second.isEmpty()) {
                    Box (
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Upcoming Tasks",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(fetchedData!!.second) { task ->
                            TaskCard(
                                task,
                                onEditTaskClick = { editTask ->
                                    onEditTaskClick(editTask)
                                },
                                onDeleteTaskClick = { taskId ->
                                    taskViewModel.deleteTask(taskId) {
                                        taskViewModel.viewModelScope.launch {
                                            fetchedData = userViewModel.fetchUserAndTasks(userEmail)
                                        }
                                    }
                                },
                                onUpdateTask = { updatedTask ->
                                    taskViewModel.updateTask(updatedTask)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TaskCard(
    task: Task,
    onEditTaskClick: (Task) -> Unit,
    onDeleteTaskClick: (Int) -> Unit,
    onUpdateTask: (Task) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var subtasks by remember { mutableStateOf(task.subtasks) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //TASK TITLE AND EXPAND/COLLAPSE BUTTON
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        Text(
                            text = "Due: ${task.dueDate}",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        )

                        Spacer(modifier = Modifier.width(24.dp))

                        Text(
                            "Sub-Tasks: ${task.subtasks.size}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    //Text("Priority: ${task.priority}", style = MaterialTheme.typography.bodySmall)
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
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
                if (subtasks.isEmpty()) {
                    Text(
                        text = "Keep up the good work!",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(subtasks) { index, subtask ->
                            HorizontalDivider(thickness = 2.dp)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Task ${index + 1}: ${subtask.name}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 16.dp)
                                )

                                Checkbox(
                                    checked = subtask.completed,
                                    //having to copy and reapply the subtask list for completing one because it wouldnt recompose previously
                                    //as room does not support mutableLists
                                    onCheckedChange = { isChecked ->
                                        subtasks = subtasks.map { currentSubtask ->
                                            if (currentSubtask.id == subtask.id) {
                                                currentSubtask.copy(completed = isChecked)
                                            } else {
                                                currentSubtask
                                            }
                                        }
                                        //Sending back the update subtask list to the task so it persists
                                        //I feel like this really isnt an efficent way of doing this but im not sure how else to do it
                                        //without having to make massive changes
                                        onUpdateTask(task.copy(subtasks = subtasks))
                                    },
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                }
            }
        }
        if (isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    onClick = { onEditTaskClick(task) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Text("Edit Task")
                }

                Button(
                    onClick = { onDeleteTaskClick(task.taskId) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Text("Delete Task")
                }
            }
        }
    }
}


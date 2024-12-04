package com.example.studypath.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studypath.model.Task
import com.example.studypath.navigation.MainScreenWithSidebar
import com.example.studypath.viewmodel.TaskViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskScreen(
    viewModel: TaskViewModel,
    onAddTaskClick: () -> Unit,
    userName: String,
    userEmail: String,
    onLogoutClick: () -> Unit
) {
    val tasks by viewModel.tasks.observeAsState(emptyList())

    MainScreenWithSidebar(
        userEmail = userEmail,
        userName = userName,
        onLogoutClick = { onLogoutClick }
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onAddTaskClick() }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Task"
                    )
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(tasks) { task ->
                    TaskItem(task)
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(task.name, style = MaterialTheme.typography.headlineMedium)
            Text("Due: ${task.dueDate}", style = MaterialTheme.typography.bodySmall)
            Text("Priority: ${task.priority}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
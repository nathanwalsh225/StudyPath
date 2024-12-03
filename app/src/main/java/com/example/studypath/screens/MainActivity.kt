package com.example.studypath.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.studypath.database.DatabaseProvider
import com.example.studypath.model.Task
import com.example.studypath.ui.theme.StudyPathTheme
import com.example.studypath.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskDao = DatabaseProvider.getDatabase(this@MainActivity).taskDao()
        val viewModel = TaskViewModel(taskDao) //Initialize ViewModel

//        lifecycleScope.launch(Dispatchers.IO) {
//            taskDao.insertTask( //dummy data
//                Task(
//                    name = "S",
//                    description = "Complete the app",
//                    dueDate = "2024-12-10",
//                    priority = 1
//                )
//            )
//        }
        enableEdgeToEdge()
        setContent {
            StudyPathTheme {
                TaskScreen(
                    viewModel = viewModel,
                    onAddTaskClick = { /* Handle Add Task click here */ }
                )
            }
        }
    }

}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskScreen(viewModel: TaskViewModel, onAddTaskClick: () -> Unit) {
    val tasks by viewModel.tasks.observeAsState(emptyList())

    val dummyTasks = listOf(
        Task(
            name = "Finish App",
            description = "Complete the app",
            dueDate = "2024-12-10",
            priority = 1
        ),
        Task(
            name = "Study Kotlin",
            description = "Brush up on Kotlin basics",
            dueDate = "2024-12-12",
            priority = 2
        )
    )

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


//@Preview
//@Composable
//fun TaskScreenPreview() {
//    StudyPathTheme {
//        TaskScreen(TaskViewModel(), {})
//    }
//}

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

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    StudyPathTheme {
//        Greeting("Android")
//    }
//}


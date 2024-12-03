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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.studypath.database.DatabaseProvider
import com.example.studypath.model.Task
import com.example.studypath.ui.theme.StudyPathTheme
import com.example.studypath.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyPathTheme {
                Surface (color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavGraph(navController)
                }
            }
        }






//       val taskDao = DatabaseProvider.getDatabase(this@MainActivity).taskDao()
//        val viewModel = TaskViewModel(taskDao) //Initialize ViewModel

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
//        enableEdgeToEdge()
//        setContent {
//            StudyPathTheme {
//                TaskScreen(
//                    viewModel = viewModel,
//                    onAddTaskClick = { /* Handle Add Task click here */ }
//                )
//            }
//        }
    }

}


//@Preview
//@Composable
//fun TaskScreenPreview() {
//    StudyPathTheme {
//        TaskScreen(TaskViewModel(), {})
//    }
//}



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


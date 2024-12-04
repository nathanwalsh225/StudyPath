package com.example.studypath.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.studypath.navigation.NavGraph
import com.example.studypath.ui.theme.StudyPathTheme

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


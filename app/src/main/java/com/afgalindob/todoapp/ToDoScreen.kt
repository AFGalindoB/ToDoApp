package com.afgalindob.todoapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.afgalindob.todoapp.data.local.db.AppDatabase
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.data.repository.OfflineTaskRepository
import com.afgalindob.todoapp.ui.NewTaskScreen
import com.afgalindob.todoapp.ui.TaskListScreen
import kotlinx.coroutines.launch
import com.afgalindob.todoapp.data.local.db.Converters
import androidx.compose.runtime.rememberCoroutineScope

enum class ToDoScreen() {
    New_Task,
    Task_List
}

@Composable
fun ToDoBottomBar(navController: NavHostController) {

    NavigationBar {

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(ToDoScreen.New_Task.name)
            },
            icon = {
                Icon(
                    painterResource(R.drawable.add),
                    contentDescription = "New Task"
                )
            },
            label = { Text(stringResource(R.string.new_option)) }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(ToDoScreen.Task_List.name)
            },
            icon = {
                Icon(
                    painterResource(R.drawable.task_list),
                    contentDescription = "Tasks"
                )
            },
            label = { Text(stringResource(R.string.tasks_option)) }
        )
    }
}

@Composable
fun ToDoApp(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val repository = remember {
        OfflineTaskRepository(
            AppDatabase.getDatabase(context).taskDao()
        )
    }

    Scaffold (
        bottomBar = { ToDoBottomBar(navController) }
    ) { innerPadding ->
        NavHost(navController = navController,
                startDestination = ToDoScreen.New_Task.name,
                modifier = Modifier.padding(innerPadding)
        ) {
            composable(ToDoScreen.New_Task.name){
                NewTaskScreen(
                    onCreateTask = { values ->
                        scope.launch {
                            val jsonValues = Converters().fromMap(values)
                            repository.insertTask(TaskEntity(values = jsonValues))
                        }
                    }
                )
            }
            composable(ToDoScreen.Task_List.name){
                TaskListScreen(repository)
            }
        }
    }
}
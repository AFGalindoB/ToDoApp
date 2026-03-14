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
import com.afgalindob.todoapp.data.repository.OfflineTaskRepository
import com.afgalindob.todoapp.ui.screens.NewTaskScreen
import com.afgalindob.todoapp.ui.screens.TaskListScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.afgalindob.todoapp.viewmodel.TaskViewModel

enum class ToDoScreen() {
    NewTask,
    TaskList
}

@Composable
fun ToDoBottomBar(navController: NavHostController) {

    NavigationBar {

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate(ToDoScreen.NewTask.name) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
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
                navController.navigate(ToDoScreen.TaskList.name) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
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

    val repository = remember {
        OfflineTaskRepository(
            AppDatabase.getDatabase(context).taskDao()
        )
    }

    val viewModel: TaskViewModel = viewModel {
        TaskViewModel(repository)
    }

    Scaffold (
        bottomBar = { ToDoBottomBar(navController) }
    ) { innerPadding ->
        NavHost(navController = navController,
                startDestination = ToDoScreen.NewTask.name,
                modifier = Modifier.padding(innerPadding)
        ) {
            composable(ToDoScreen.NewTask.name){
                NewTaskScreen(
                    onCreateTask = { values -> viewModel.createTask(values) }
                )
            }
            composable(ToDoScreen.TaskList.name){
                TaskListScreen(viewModel)
            }
        }
    }
}
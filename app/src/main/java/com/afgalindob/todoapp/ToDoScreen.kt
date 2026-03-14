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
import com.afgalindob.todoapp.ui.NewTaskScreen
import com.afgalindob.todoapp.ui.TaskListScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.afgalindob.todoapp.viewmodel.TaskViewModel

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
                navController.navigate(ToDoScreen.New_Task.name) {
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
                navController.navigate(ToDoScreen.Task_List.name) {
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
                startDestination = ToDoScreen.New_Task.name,
                modifier = Modifier.padding(innerPadding)
        ) {
            composable(ToDoScreen.New_Task.name){
                NewTaskScreen(
                    onCreateTask = { values -> viewModel.createTask(values) }
                )
            }
            composable(ToDoScreen.Task_List.name){
                TaskListScreen(viewModel)
            }
        }
    }
}
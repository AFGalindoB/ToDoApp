package com.afgalindob.todoapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.afgalindob.todoapp.data.Task
import com.afgalindob.todoapp.data.TaskRepository
import com.afgalindob.todoapp.ui.NewTaskScreen
import com.afgalindob.todoapp.ui.TaskListScreen

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
    Scaffold (
        bottomBar = { ToDoBottomBar(navController) }
    ) { innerPadding ->
        NavHost(navController = navController,
                startDestination = ToDoScreen.New_Task.name,
                modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = ToDoScreen.New_Task.name){
                NewTaskScreen(
                    onCreateTask = { values ->
                        TaskRepository.addTask(Task(values = values))
                    }
                )
            }
            composable(route = ToDoScreen.Task_List.name){
                TaskListScreen()
            }
        }
    }
}
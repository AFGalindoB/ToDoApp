package com.afgalindob.todoapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.afgalindob.todoapp.data.local.db.AppDatabase
import com.afgalindob.todoapp.data.repository.OfflineTaskRepository
import com.afgalindob.todoapp.ui.screens.TaskListScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.afgalindob.todoapp.ui.screens.AccountScreen
import com.afgalindob.todoapp.viewmodel.TaskViewModel

enum class ToDoScreen() {
    TaskList,
    Account
}

@Composable
fun ToDoBottomBar(navController: NavHostController) {

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar {

        NavigationBarItem(
            selected = currentRoute == ToDoScreen.TaskList.name,
            onClick = {
                if (currentRoute != ToDoScreen.TaskList.name) {
                    navController.navigate(ToDoScreen.TaskList.name) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
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

        NavigationBarItem(
            selected = currentRoute == ToDoScreen.Account.name,
            onClick = {
                if (currentRoute != ToDoScreen.Account.name){
                    navController.navigate(ToDoScreen.Account.name) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            },
            icon = {
                Icon(
                    painterResource(R.drawable.account),
                    contentDescription = "Tasks"
                )
            },
            label = { Text(stringResource(R.string.account_option)) }

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
    val textColor: Color = Color.White
    val backgroundColor: Color = Color.Black

    Scaffold (
        bottomBar = { ToDoBottomBar(navController) }
    ) { innerPadding ->
        NavHost(navController = navController,
                startDestination = ToDoScreen.TaskList.name,
                modifier = Modifier.padding(innerPadding)
        ) {
            composable(ToDoScreen.TaskList.name){
                TaskListScreen(viewModel, textColor, backgroundColor)
            }
            composable(ToDoScreen.Account.name){
                AccountScreen(textColor, backgroundColor)
            }
        }
    }
}
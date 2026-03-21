package com.afgalindob.todoapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.afgalindob.todoapp.data.local.db.AppDatabase
import com.afgalindob.todoapp.data.repository.OfflineTaskRepository
import com.afgalindob.todoapp.ui.screens.TaskListScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.afgalindob.todoapp.navigation.ToDoBottomBar
import com.afgalindob.todoapp.ui.screens.AccountScreen
import com.afgalindob.todoapp.viewmodel.TaskViewModel

enum class ToDoScreen() {
    TaskList,
    Account
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
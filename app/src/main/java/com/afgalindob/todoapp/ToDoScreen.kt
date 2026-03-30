package com.afgalindob.todoapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.afgalindob.todoapp.ui.screens.TaskListScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.afgalindob.todoapp.navigation.ToDoBottomBar
import com.afgalindob.todoapp.ui.screens.AccountScreen
import com.afgalindob.todoapp.ui.screens.NotesListScreen
import com.afgalindob.todoapp.viewmodel.NoteViewModel
import com.afgalindob.todoapp.viewmodel.TaskViewModel

enum class ToDoScreen() {
    TaskList,
    NoteList,
    Account
}

@Composable
fun ToDoApp(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val container = (context.applicationContext as TodoApplication).container

    val taskViewModel: TaskViewModel = viewModel {
        TaskViewModel(container.taskRepository)
    }

    val noteViewModel: NoteViewModel = viewModel {
        NoteViewModel(container.noteRepository)
    }

    Scaffold (
        bottomBar = { ToDoBottomBar(navController) }
    ) { innerPadding ->
        NavHost(navController = navController,
                startDestination = ToDoScreen.TaskList.name,
                modifier = Modifier.padding(innerPadding)
        ) {
            composable(ToDoScreen.TaskList.name){
                TaskListScreen(taskViewModel)
            }
            composable(ToDoScreen.Account.name){
                AccountScreen()
            }
            composable(ToDoScreen.NoteList.name) {
                NotesListScreen(noteViewModel)
            }
        }
    }
}
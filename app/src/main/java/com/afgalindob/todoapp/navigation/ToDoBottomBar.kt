package com.afgalindob.todoapp.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.ToDoScreen

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
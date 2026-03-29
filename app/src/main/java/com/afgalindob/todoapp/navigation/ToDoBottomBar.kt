package com.afgalindob.todoapp.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.ToDoScreen
import com.afgalindob.todoapp.ui.theme.AccentPrimary
import com.afgalindob.todoapp.ui.theme.OnAccentPrimary
import com.afgalindob.todoapp.ui.theme.OnSurfacePrimary
import com.afgalindob.todoapp.ui.theme.OnSurfaceSecondary
import com.afgalindob.todoapp.ui.theme.SurfaceContainer

@Composable
fun ToDoBottomBar(navController: NavHostController) {

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationBar (
        containerColor = SurfaceContainer,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(ToDoScreen.TaskList.name, R.drawable.task_list, R.string.tasks_option),
            Triple(ToDoScreen.Account.name, R.drawable.account, R.string.account_option)
        )
        items.forEach { (route, iconRes, labelRes) ->
            val isSelected = currentRoute == route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = stringResource(labelRes)
                    )
                },
                label = { Text(stringResource(labelRes)) },
                colors = NavigationBarItemDefaults.colors(
                    // --- COLORES SEMÁNTICOS ---
                    // La píldora de selección (fondo del icono activo)
                    selectedIconColor = OnAccentPrimary,
                    indicatorColor = AccentPrimary,

                    // Icono y Texto cuando están activos
                    selectedTextColor = OnSurfacePrimary,

                    // Icono y Texto cuando NO están activos (más apagados)
                    unselectedIconColor = OnSurfaceSecondary,
                    unselectedTextColor = OnSurfaceSecondary
                )
            )
        }
    }
}
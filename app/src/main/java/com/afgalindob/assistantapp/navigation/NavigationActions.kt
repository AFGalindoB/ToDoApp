package com.afgalindob.assistantapp.navigation

import androidx.compose.material3.DrawerState
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NavigationActions(
    private val navController: NavHostController,
    private val scope: CoroutineScope,
    private val drawerState: DrawerState,
    private val onStartTransition: suspend (Any) -> Unit
) {
    fun navigateToHome() {
        scope.launch {
            drawerState.close()
            onStartTransition(HomeGraph)
            navController.navigate(HomeGraph) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
                restoreState = false
            }
        }
    }

    fun navigateToTrash() {
        scope.launch {
            drawerState.close()
            onStartTransition(TrashGraph)
            navController.navigate(TrashGraph) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    fun navigateToAccount() {
        scope.launch {
            drawerState.close()
            onStartTransition(AccountGraph)
            navController.navigate(AccountGraph) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    fun navigateInHome(destination: Any) {
        scope.launch {
            onStartTransition(destination)
            navController.navigate(destination) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}
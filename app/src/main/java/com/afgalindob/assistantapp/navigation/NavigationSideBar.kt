package com.afgalindob.assistantapp.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.afgalindob.assistantapp.ui.theme.SurfaceContainer
import com.afgalindob.assistantapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun NavigationSideBar(
    drawerState: DrawerState,
    navActions: NavigationActions,
    currentDestination: NavDestination?,
    scope: CoroutineScope,
) {
    val isHomeSelected = currentDestination?.hasRoute<TaskList>() == true ||
            currentDestination?.hasRoute<NoteList>() == true
    val isTrashSelected = currentDestination?.hasRoute<TrashScreen>() == true
    val isAccountSelected = currentDestination?.hasRoute<Account>() == true

    ModalDrawerSheet(
        drawerContainerColor = SurfaceContainer
    ) {
        Spacer(Modifier.height(12.dp))

        // ITEM HOME
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.home)) },
            selected = isHomeSelected,
            onClick = {
                if (isHomeSelected) {
                    scope.launch { drawerState.close() }
                } else {
                    navActions.navigateToHome()
                }
            },
            icon = { Icon(painterResource(R.drawable.home), contentDescription = null) }
        )

        // ITEM TRASH
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.trash)) },
            selected = isTrashSelected,
            onClick = {
                if (isTrashSelected) {
                    scope.launch { drawerState.close() }
                } else {
                    navActions.navigateToTrash()
                }
            },
            icon = { Icon(painterResource(R.drawable.trash), contentDescription = null) }
        )

        // ITEM ACCOUNT
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.account_option)) },
            selected = isAccountSelected,
            onClick = {
                if (isAccountSelected) {
                    scope.launch { drawerState.close() }
                } else {
                    navActions.navigateToAccount()
                }
            },
            icon = { Icon(painterResource(R.drawable.account), contentDescription = null) }
        )
    }
}
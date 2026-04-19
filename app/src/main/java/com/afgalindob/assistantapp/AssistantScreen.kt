package com.afgalindob.assistantapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.afgalindob.assistantapp.ui.screens.TaskListScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.afgalindob.assistantapp.navigation.HomeGraph
import com.afgalindob.assistantapp.navigation.TaskList
import com.afgalindob.assistantapp.navigation.NoteList
import com.afgalindob.assistantapp.navigation.NavigationBottomBar
import com.afgalindob.assistantapp.navigation.TrashGraph
import com.afgalindob.assistantapp.ui.screens.AccountScreen
import com.afgalindob.assistantapp.ui.screens.NotesListScreen
import com.afgalindob.assistantapp.ui.theme.SurfaceContainer
import com.afgalindob.assistantapp.viewmodel.NoteViewModel
import com.afgalindob.assistantapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import androidx.navigation.compose.navigation
import com.afgalindob.assistantapp.navigation.Account
import com.afgalindob.assistantapp.navigation.TrashScreen
import com.afgalindob.assistantapp.ui.screens.TrashScreen
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.graphics.graphicsLayer
import com.afgalindob.assistantapp.navigation.NavigationSideBar
import com.afgalindob.assistantapp.navigation.NavigationActions
import com.afgalindob.assistantapp.ui.theme.OnSurfaceSecondary
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.LaunchedEffect
import com.afgalindob.assistantapp.data.container.AppContainer
import com.afgalindob.assistantapp.navigation.AccountGraph
import com.afgalindob.assistantapp.viewmodel.TrashViewModel

enum class AppTransitionState {
    IDLE,
    TRANSITIONING,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantApp(container: AppContainer, isAppReady: Boolean) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val taskViewModel: TaskViewModel = viewModel {
        TaskViewModel(container.taskRepository)
    }
    val noteViewModel: NoteViewModel = viewModel {
        NoteViewModel(container.noteRepository)
    }
    val trashViewModel: TrashViewModel = viewModel {
        TrashViewModel(container.trashRepository)
    }

    val curtainAlpha = remember { Animatable(0f) }
    var isContentReady by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var pendingDestination by remember { mutableStateOf<Any?>(null) }
    var transitionState by remember { mutableStateOf(AppTransitionState.IDLE) }
    var topBarActions by remember { mutableStateOf<(@Composable RowScope.() -> Unit)>({}) }

    val isAtDestiny by remember(currentDestination, pendingDestination) {
        derivedStateOf {
            val pending = pendingDestination ?: return@derivedStateOf true

            currentDestination?.hasRoute(pending::class) == true ||
                    currentDestination?.parent?.hasRoute(pending::class) == true
        }
    }
    val isChanging by remember(drawerState.isOpen, isAtDestiny, isContentReady, transitionState) {
        derivedStateOf {
            drawerState.isOpen ||
                    !isAtDestiny ||
                    !isContentReady ||
                    transitionState == AppTransitionState.TRANSITIONING
        }
    }

    val navActions = remember(navController, drawerState, scope) {
        NavigationActions(
            navController = navController,
            scope = scope,
            drawerState = drawerState,
            onStartTransition = { route ->
                topBarActions = {}
                isContentReady = false
                pendingDestination = route
                transitionState = AppTransitionState.TRANSITIONING

                curtainAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(300)
                )
            }
        )
    }

    LaunchedEffect(isContentReady, isAtDestiny) {
        if (isContentReady && isAtDestiny && transitionState == AppTransitionState.TRANSITIONING) {
            curtainAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(400)
            )
            transitionState = AppTransitionState.IDLE
            pendingDestination = null
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            NavigationSideBar(
                drawerState = drawerState,
                currentDestination = currentDestination,
                navActions = navActions,
                scope = scope
            )
        }
    ) {
        Box( modifier = Modifier.fillMaxSize() ){
            Scaffold (
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    if (!isChanging){
                                        scope.launch { drawerState.open() }
                                    }
                                },
                                enabled = !isChanging
                            ) {
                                Icon(
                                    painterResource(R.drawable.menu),
                                    contentDescription = "Open Drawer",
                                    tint = OnSurfaceSecondary.copy(
                                        alpha = if (isChanging) 0.5f else 1f
                                    )
                                )
                            }
                        },
                        actions = { topBarActions() }
                    )
                },
                bottomBar = {
                    val inHome = currentDestination?.hasRoute<TaskList>() == true ||
                            currentDestination?.hasRoute<NoteList>() == true
                    if(inHome){
                        val isDisabled = drawerState.isOpen || drawerState.isAnimationRunning || isChanging
                        NavigationBottomBar(
                            navController = navController,
                            isInteractionDisabled = isDisabled,
                            navActions = navActions
                        )
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = HomeGraph,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .background(SurfaceContainer),
                    enterTransition = { androidx.compose.animation.EnterTransition.None },
                    exitTransition = { androidx.compose.animation.ExitTransition.None }
                ) {
                    navigation<HomeGraph>(startDestination = TaskList) {
                        composable<TaskList> {
                            TaskListScreen(
                                viewModel = taskViewModel,
                                isAppReady = isAppReady,
                                onRendered = { isContentReady = true },
                                updateTopBar = { topBarActions = it }
                            )
                        }
                        composable<NoteList> {
                            NotesListScreen(
                                viewModel = noteViewModel,
                                onRendered = { isContentReady = true }
                            )
                        }
                    }
                    navigation<AccountGraph>(startDestination = Account) {
                        composable<Account> {
                            AccountScreen(
                                onRendered = { isContentReady = true }
                            )
                        }
                    }
                    navigation<TrashGraph>(startDestination = TrashScreen) {
                        composable<TrashScreen> {
                            TrashScreen(
                                viewModel = trashViewModel,
                                onRendered = { isContentReady = true }
                            )
                        }
                    }
                }
            }
            if (curtainAlpha.value > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(10f)
                        .graphicsLayer { alpha = curtainAlpha.value }
                        .background(SurfaceContainer)
                )
            }
        }
    }
}
package com.afgalindob.assistantapp.ui.screens

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import com.afgalindob.assistantapp.ui.components.cards.TaskCard
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.afgalindob.assistantapp.R
import com.afgalindob.assistantapp.ui.dialogs.TaskUpserDialog
import com.afgalindob.assistantapp.viewmodel.TaskViewModel
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.afgalindob.assistantapp.domain.TaskDomain
import com.afgalindob.assistantapp.domain.validation.ValidationError
import com.afgalindob.assistantapp.navigation.FormMode
import com.afgalindob.assistantapp.ui.components.EntitySnackbar
import com.afgalindob.assistantapp.ui.components.SectionHeader
import com.afgalindob.assistantapp.ui.components.cards.TaskEvent
import com.afgalindob.assistantapp.ui.dialogs.FilterBottomSheet
import com.afgalindob.assistantapp.ui.theme.AccentPrimary
import com.afgalindob.assistantapp.ui.theme.AccentSecondary
import com.afgalindob.assistantapp.ui.theme.BackgroundColor
import com.afgalindob.assistantapp.ui.theme.OnAccentPrimary
import com.afgalindob.assistantapp.ui.theme.OnAccentSecondary
import com.afgalindob.assistantapp.ui.theme.OnSurfacePrimary

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    isAppReady: Boolean,
    onRendered: () -> Unit,
    updateTopBar: (@Composable RowScope.() -> Unit) -> Unit
){
    var filterDialog by remember { mutableStateOf(false)}

    DisposableEffect(Unit) {
        // Al entrar: Ponemos el botón
        updateTopBar {
            IconButton(onClick = {
                filterDialog = true
            }) {
                Icon(
                    painter = painterResource(R.drawable.filter),
                    contentDescription = "Filter",
                    tint = OnSurfacePrimary
                )
            }
        }

        onDispose { updateTopBar { } }
    }
    var hasFinishedDrawing by remember { mutableStateOf(false) }

    ReportDrawnWhen { hasFinishedDrawing && isAppReady }

    LaunchedEffect(Unit) {
        // Incrementamos la espera para evitar condiciones de carrera en el Benchmark
        // dando tiempo a que el bot detecte el estado inicial de la actividad.
        repeat(5) { withFrameNanos { } }
        delay(100)

        onRendered()

        hasFinishedDrawing = true
    }

    val tasks by viewModel.tasksDomain.collectAsState()
    val tasksBySection by viewModel.tasksBySection.collectAsState()
    val showCompleted by viewModel.showCompletedState.collectAsState()

    var expandedTaskId by remember { mutableStateOf<Long?>(null) }
    val isAnyExpanded by remember { derivedStateOf { expandedTaskId != null } }
    val onExpandAction = remember {
        { id: Long -> expandedTaskId = if (expandedTaskId == id) null else id }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var deletingTask by remember { mutableStateOf<TaskDomain?>(null) }

    var dialogMode by remember { mutableStateOf<String?>(null) }
    var editingTask by remember { mutableStateOf<TaskDomain?>(null) }
    val taskErrors = remember { mutableStateMapOf<String, ValidationError>() }

    val onToggle = remember(viewModel) {
        { task: TaskDomain, completed: Boolean -> viewModel.toggleTaskCompleted(task, completed) }
    }
    val onEditAction = remember {
        { task: TaskDomain ->
            editingTask = task
            dialogMode = FormMode.Edit.name
            taskErrors.clear()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundColor) {

        Box(modifier = Modifier.fillMaxSize()){
            Column {
                if (tasks.isEmpty()){
                    Text(
                        stringResource(R.string.list_placeholder, stringResource(R.string.tasks).lowercase()),
                        style = MaterialTheme.typography.displayLarge,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                LazyColumn {
                    tasksBySection.forEach { (section, tasks) ->
                        // Header de la sección
                        item(key = "section_$section", contentType = "header") {
                            SectionHeader(section)
                        }

                        // Items de la sección
                        items(
                            items = tasks,
                            key = { it.id }
                        ) { task ->
                            val isExpanded = expandedTaskId == task.id
                            TaskCard(
                                task = task,
                                date = task.date,
                                expanded = isExpanded,
                                onExpand = { onExpandAction(task.id) },
                                anyCardExpanded = isAnyExpanded,
                                onEvent = { event ->
                                    if (event is TaskEvent.Delete) {
                                        deletingTask = task
                                    }
                                    if (event is TaskEvent.ToggleCompleted) {
                                        onToggle(task, event.completed)
                                    }
                                    if (event is TaskEvent.Edit) {
                                        onEditAction(task)
                                    }

                                },
                                actionArea = {
                                    IconButton(
                                        onClick = { onEditAction(task) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(AccentSecondary)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.edit),
                                            contentDescription = "Edit Task",
                                            tint = OnAccentSecondary
                                        )
                                    }
                                }
                            )
                        }
                    }

                    // Footer de la lista
                    item { Spacer(Modifier.height(50.dp)) }
                    item {
                        if (!tasks.isEmpty()) {
                            Text(
                                stringResource(R.string.end_of_list) + " " + stringResource(R.string.tasks),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            EntitySnackbar(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            FloatingActionButton(
                onClick = {
                    editingTask = null
                    dialogMode = FormMode.New.name
                    taskErrors.clear()
                },
                containerColor = AccentPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "Add Task",
                    tint = OnAccentPrimary
                )
            }

        }

        dialogMode?.let{
            TaskUpserDialog(
                task = if (dialogMode == FormMode.Edit.name) editingTask else null,
                errors = taskErrors,
                onConfirm = { formState ->

                    // Validación
                    val validationErrors = viewModel.validate(formState)
                    taskErrors.clear()
                    taskErrors.putAll(validationErrors)

                    if (validationErrors.isEmpty()) {

                        if (dialogMode == FormMode.New.name) {
                            viewModel.createTask(formState)
                        } else if (dialogMode == FormMode.Edit.name && editingTask != null) {
                            viewModel.updateTask(editingTask!!, formState)
                        }

                        // Cerrar diálogo
                        dialogMode = null
                        editingTask = null
                    }
                },
                onDismiss = {
                    dialogMode = null
                    editingTask = null
                }
            )
        }
        val undoLabel = stringResource(R.string.undo)
        val messageLabel = stringResource(R.string.task) + " " + stringResource(R.string.sent_to_trash).lowercase()
        LaunchedEffect(deletingTask) {
            val taskToHandle = deletingTask // Copia local para evitar problemas de referencia
            if (taskToHandle != null) {
                viewModel.softDeleteTask(taskToHandle)

                val result = snackbarHostState.showSnackbar(
                    message = messageLabel,
                    actionLabel = undoLabel,
                    duration = SnackbarDuration.Short
                )

                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.restoreTask(taskToHandle)
                }

                deletingTask = null
            }
        }
        if (filterDialog) {
            FilterBottomSheet(
                showCompleted = showCompleted,
                onCompletedChanged = { viewModel.toggleShowCompleted(it) },
                onDismiss = { filterDialog = false }
            )
        }
    }
}

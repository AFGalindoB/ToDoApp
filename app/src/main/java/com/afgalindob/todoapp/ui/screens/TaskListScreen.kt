package com.afgalindob.todoapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import com.afgalindob.todoapp.ui.components.TaskCard
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.ui.dialogs.DeleteTaskDialog
import com.afgalindob.todoapp.ui.dialogs.TaskDialog
import com.afgalindob.todoapp.viewmodel.TaskViewModel
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.afgalindob.todoapp.data.mapper.TaskMapper.toFormState
import com.afgalindob.todoapp.domain.TaskDomain
import com.afgalindob.todoapp.ui.dialogs.FilterBottomSheet
import com.afgalindob.todoapp.ui.theme.AccentPrimary
import com.afgalindob.todoapp.ui.theme.BackgroundColor
import com.afgalindob.todoapp.ui.theme.OnAccentPrimary
import com.afgalindob.todoapp.ui.theme.OnSurfacePrimary
import com.afgalindob.todoapp.ui.theme.OnSurfaceSecondary
import com.afgalindob.todoapp.ui.theme.SurfaceVariant

enum class TypeTaskDialog(){
    New,
    Edit
}

@Composable
fun TaskListScreen(viewModel: TaskViewModel, ){

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundColor) {

        val tasks by viewModel.tasksDomain.collectAsState()
        val tasksBySection by viewModel.tasksBySection.collectAsState()
        val showCompleted by viewModel.showCompletedState.collectAsState()

        var expandedTaskId by remember { mutableStateOf<Long?>(null) }

        var dialogMode by remember { mutableStateOf<String?>(null) }
        var editingTask by remember { mutableStateOf<TaskDomain?>(null) }
        var deletingTask by remember { mutableStateOf<TaskDomain?>(null) }
        val taskErrors = remember { mutableStateMapOf<String,String>() }
        var filterDialog by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()){
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp), // opcional
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f)) // empuja el botón a la derecha
                    IconButton(onClick = { filterDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.filter),
                            contentDescription = "Filter",
                            tint = OnSurfacePrimary
                        )
                    }
                }

                if (tasks.isEmpty()){
                    Text(
                        stringResource(R.string.task_list_placeholder),
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
                        item {
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ){

                                HorizontalDivider(
                                    color = SurfaceVariant,
                                    thickness = 1.dp,
                                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                )

                                Text(
                                    text = stringResource(section),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = OnSurfaceSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )

                                HorizontalDivider(
                                    color = SurfaceVariant,
                                    thickness = 1.dp,
                                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                )
                            }
                        }

                        // Items de la sección
                        items(tasks) { task ->
                            TaskCard(
                                task = task,
                                expanded = expandedTaskId == task.id, // solo la abierta se expande
                                onExpand = {
                                    expandedTaskId = if (expandedTaskId == task.id) null else task.id
                                },
                                anyCardExpanded = expandedTaskId != null,
                                onToggleCompleted = { completed ->
                                    val updatedForm = task.toFormState().copy(completed = completed)
                                    viewModel.updateTask(task, updatedForm)
                                },
                                onEdit = {
                                    editingTask = task
                                    dialogMode = TypeTaskDialog.Edit.name
                                    taskErrors.clear()
                                },
                                onDelete = { deletingTask = task }
                            )
                        }
                    }
                    item {
                        Spacer(Modifier.height(50.dp))
                    }
                    item {
                        if (!tasks.isEmpty()) {
                            Text(
                                stringResource(R.string.end_of_tasks),
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

            FloatingActionButton(
                onClick = {
                    editingTask = null
                    dialogMode = TypeTaskDialog.New.name
                    taskErrors.clear()
                },
                containerColor = AccentPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(painter = painterResource(R.drawable.add),
                    contentDescription = "Add Task",
                    tint = OnAccentPrimary
                )
            }

        }

        if (dialogMode != null) {
            TaskDialog(
                task = if (dialogMode == TypeTaskDialog.Edit.name) editingTask else null,
                errors = taskErrors,
                onConfirm = { formState ->

                    // Validación
                    val validationErrors = viewModel.validate(formState)
                    taskErrors.clear()
                    taskErrors.putAll(validationErrors)

                    if (validationErrors.isEmpty()) {

                        if (dialogMode == TypeTaskDialog.New.name) {
                            viewModel.createTask(formState)
                        } else if (dialogMode == TypeTaskDialog.Edit.name && editingTask != null) {
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

        deletingTask?.let { task ->
            DeleteTaskDialog(
                task = task,

                onConfirm = {
                    viewModel.deleteTask(task)
                    deletingTask = null
                },

                onDismiss = {
                    deletingTask = null
                }
            )
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

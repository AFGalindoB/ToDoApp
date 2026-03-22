package com.afgalindob.todoapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import com.afgalindob.todoapp.ui.components.TaskCard
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import com.afgalindob.todoapp.data.mapper.TaskMapper.toFormState
import com.afgalindob.todoapp.domain.TaskDomain
import com.afgalindob.todoapp.viewmodel.TaskFilter

enum class TypeTaskDialog(){
    New,
    Edit
}

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    textColor: Color,
    backgroundColor: Color
){

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {

        val tasks by viewModel.tasksDomain.collectAsState()
        val tasksBySection by viewModel.tasksBySection.collectAsState()

        var dialogMode by remember { mutableStateOf<String?>(null) }
        var editingTask by remember { mutableStateOf<TaskDomain?>(null) }
        var deletingTask by remember { mutableStateOf<TaskDomain?>(null) }
        val taskErrors = remember { mutableStateMapOf<String,String>() }

        Box(modifier = Modifier.fillMaxSize()){
            Column {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Button(onClick = { viewModel.setFilter(TaskFilter.ALL) }) {
                        Text(stringResource(R.string.filter_all))
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(onClick = { viewModel.setFilter(TaskFilter.PENDING) }) {
                        Text(stringResource(R.string.filter_pending))
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(onClick = { viewModel.setFilter(TaskFilter.BY_DATE) }) {
                        Text(stringResource(R.string.filter_by_date))
                    }
                }
                if (tasks.isEmpty()){
                    Text(
                        stringResource(R.string.task_list_placeholder),
                        color = textColor
                    )
                }
                LazyColumn {
                    tasksBySection.forEach { (section, tasks) ->
                        // Header de la sección
                        item {
                            Text(
                                text = stringResource(section),
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        // Items de la sección
                        items(tasks) { task ->
                            TaskCard(
                                task = task,
                                onToggleCompleted = { completed ->
                                    val updatedForm = task.toFormState().copy(completed = completed)
                                    viewModel.updateTask(task, updatedForm)
                                },
                                textColor = textColor,
                                onEdit = {
                                    editingTask = task
                                    dialogMode = TypeTaskDialog.Edit.name
                                    taskErrors.clear()
                                },
                                onDelete = { deletingTask = task }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            FloatingActionButton(
                onClick = {
                    editingTask = null
                    dialogMode = TypeTaskDialog.New.name
                    taskErrors.clear()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(painter = painterResource(R.drawable.add),
                    contentDescription = "Add Task"
                )
            }
        }

        if (dialogMode != null) {
            TaskDialog(
                task = if (dialogMode == TypeTaskDialog.Edit.name) editingTask else null,
                errors = taskErrors,
                colorText = textColor,
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
    }
}

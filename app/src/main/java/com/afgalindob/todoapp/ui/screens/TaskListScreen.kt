package com.afgalindob.todoapp.ui.screens

import androidx.compose.foundation.layout.Box
import com.afgalindob.todoapp.ui.components.TaskCard
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.afgalindob.todoapp.data.local.db.Converters
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.ui.dialogs.DeleteTaskDialog
import com.afgalindob.todoapp.ui.dialogs.TaskDialog
import com.afgalindob.todoapp.viewmodel.TaskViewModel
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.Alignment

@Composable
fun TaskListScreen(viewModel: TaskViewModel){

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {

        val tasks by viewModel.tasks.collectAsState()

        var dialogMode by remember { mutableStateOf<String?>(null) }
        var editingTask by remember { mutableStateOf<TaskEntity?>(null) }
        var deletingTask by remember { mutableStateOf<TaskEntity?>(null) }
        val taskErrors = remember { mutableStateMapOf<String,String>() }

        Box(modifier = Modifier.fillMaxSize()){
            LazyColumn {
                item { Text(stringResource(R.string.task_list_title)) }

                items(tasks) {task ->

                    TaskCard(
                        task = task,

                        onToggleCompleted = { completed ->
                            val values = Converters().toMap(task.values).toMutableMap()
                            values["completed"] = completed.toString()

                            viewModel.updateTask(task, values)
                        },

                        onEdit = {
                            editingTask = task
                            dialogMode = "edit"
                            taskErrors.clear()
                        },

                        onDelete = {
                            deletingTask = task
                        }
                    )
                }
            }
            FloatingActionButton(
                onClick = {
                    editingTask = null
                    dialogMode = "new"
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
                task = if (dialogMode == "edit") editingTask else null,
                errors = taskErrors,
                onConfirm = { values ->
                    val validationErrors = viewModel.validate(values)
                    taskErrors.clear()
                    taskErrors.putAll(validationErrors)

                    if (validationErrors.isEmpty()) {
                        if (dialogMode == "new") {
                            viewModel.createTask(values)
                        } else if (dialogMode == "edit" && editingTask != null) {
                            viewModel.updateTask(editingTask!!, values)
                        }
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

package com.afgalindob.todoapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
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
import com.afgalindob.todoapp.schema.TaskSchema
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

                    val valuesMap = Converters().toMap(task.values)

                    Column( modifier = Modifier.fillMaxWidth().padding(16.dp) )
                    {
                        // Renderizar los campos del formulario
                        TaskSchema.fields.forEach { field ->
                            val value = valuesMap[field.key] ?: ""
                            Text(text = stringResource(field.labelRes) + ": " + value)
                        }

                        Row {
                            // Boton Eliminar
                            Button(onClick = { deletingTask = task }) {
                                Row{
                                    Icon(
                                        painter = painterResource(R.drawable.delete),
                                        contentDescription = "Delete Task"
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(stringResource(R.string.delete_task))
                                }
                            }

                            Spacer(modifier = Modifier.width(5.dp))

                            // Boton Editar
                            Button(
                                onClick = {
                                    editingTask = task
                                    dialogMode = "edit"
                                    taskErrors.clear()
                                }
                            ) {
                                Row{
                                    Icon(
                                        painter = painterResource(R.drawable.edit),
                                        contentDescription = "Edit Task"
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(stringResource(R.string.edit_task))
                                }
                            }
                        }
                    }
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

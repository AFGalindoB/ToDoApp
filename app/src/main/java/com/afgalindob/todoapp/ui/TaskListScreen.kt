package com.afgalindob.todoapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.afgalindob.todoapp.data.TaskRepository
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.data.Task

@Composable
fun TaskListScreen(){
    Surface(modifier = Modifier
        .fillMaxSize(),
        color = Color.Black
    ) {
        val tasks = TaskRepository.tasks
        var editingTask by remember { mutableStateOf<Task?>(null) }

        LazyColumn (){
            item {Text(stringResource(R.string.task_list_title))}

            items(tasks) {task ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ){
                    Text(stringResource(R.string.title_task) + ": " + task.title)
                    Text(stringResource(R.string.description) + ": " + task.description)

                    Row {
                        Button(
                            onClick = { TaskRepository.removeTask(task) }
                        ) {
                            Row(){
                                Icon(
                                    painter = painterResource(R.drawable.delete),
                                    contentDescription = "Delete Task"
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(stringResource(R.string.delete_task))
                            }
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Button(
                            onClick = { editingTask = task }
                        ) {
                            Row(){
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
        editingTask?.let { task ->

            var title by remember { mutableStateOf(task.title) }
            var description by remember { mutableStateOf(task.description) }

            AlertDialog(
                onDismissRequest = { editingTask = null },

                title = { Text(stringResource(R.string.edit_task)) },

                text = {
                    Column {
                        TextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(stringResource(R.string.title_task)) }
                        )

                        TextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(stringResource(R.string.description)) }
                        )
                    }
                },

                confirmButton = {
                    Button(
                        onClick = {
                            TaskRepository.updateTask(
                                task.id,
                                title,
                                description
                            )
                            editingTask = null
                        }
                    ) { Text(stringResource(R.string.apply)) }
                },

                dismissButton = {
                    Button(onClick = { editingTask = null })
                    {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskListScreenPreview(){
    TaskListScreen()
}
package com.afgalindob.todoapp.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.domain.TaskDomain

@Composable
fun DeleteTaskDialog(
    task: TaskDomain,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    val title = task.title

    AlertDialog(
        onDismissRequest = onDismiss,

        title = { Text(stringResource(R.string.delete_task)) },

        text = {
            Text(stringResource(R.string.delete_task_dialog)+ "\n\n"+ title)

        },

        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.delete_option))
            }
        },

        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
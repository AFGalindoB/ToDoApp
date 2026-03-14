package com.afgalindob.todoapp.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.afgalindob.todoapp.data.local.db.Converters
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.schema.TaskSchema


@Composable
fun EditTaskDialog(
    task: TaskEntity,
    onConfirm: (Map<String,String>) -> Unit,
    onDismiss: () -> Unit
) {

    val values = remember(task) {
        mutableStateMapOf<String,String>().apply {
            putAll(Converters().toMap(task.values))
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = { Text(stringResource(R.string.edit_task)) },

        text = {
            Column {
                TaskSchema.fields.forEach { field ->
                    field.type.RenderInput(
                        field = field,
                        values = values
                    )
                }
            }
        },

        confirmButton = {
            Button(
                onClick = { onConfirm(values.toMap()) }
            ) {
                Text(stringResource(R.string.apply))
            }
        },

        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
package com.afgalindob.todoapp.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.data.mapper.TaskMapper.toFormState
import com.afgalindob.todoapp.schema.TaskDomain
import com.afgalindob.todoapp.schema.TaskSchema

@Composable 
fun TaskDialog(
    task: TaskDomain? = null,
    colorText: Color,
    errors: Map<String,String> = emptyMap(),
    onConfirm: (Map<String,String>) -> Unit,
    onDismiss: () -> Unit
) {

    val values = remember(task) {
        mutableStateMapOf<String, String>().apply {
            val form = task?.toFormState()
            if (form != null) {
                this["title"] = form.title
                this["content"] = form.content
                this["date"] = form.date?.toString() ?: ""
                this["completed"] = form.completed.toString()
            } else {
                TaskSchema.fields.forEach { field ->
                    this[field.key] = ""
                }
            }
        }
    }
    val isEditing = task != null

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = if (isEditing)
                    stringResource(R.string.edit_task)
                else
                    stringResource(R.string.new_task_title),
                color = colorText
            )
        },

        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp) // espacio interior para que no pegue
            ) {
                TaskSchema.fields.forEach { field ->

                    field.type.RenderInput(
                        field = field,
                        values = values,
                        onValueChange = { key, value -> values[key] = value }
                    )

                    errors[field.key]?.let { errorMessage ->
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        },

        confirmButton = {
            Button(onClick = { onConfirm(values.toMap()) }) {
                Text(
                    text = if (isEditing)
                        stringResource(R.string.apply)
                    else
                        stringResource(R.string.add_task)
                )
            }
        },

        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
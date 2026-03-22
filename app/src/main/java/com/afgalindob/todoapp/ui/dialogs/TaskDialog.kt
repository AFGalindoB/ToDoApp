package com.afgalindob.todoapp.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.domain.TaskDomain
import com.afgalindob.todoapp.domain.TaskFormState
import com.afgalindob.todoapp.utils.DateUtils
import java.time.LocalDate

@Composable 
fun TaskDialog(
    task: TaskDomain? = null,
    colorText: Color,
    errors: Map<String,String> = emptyMap(),
    onConfirm: (TaskFormState) -> Unit,
    onDismiss: () -> Unit
) {

    val isEditing = task != null
    var title by remember { mutableStateOf(task?.title ?: "") }
    var content by remember { mutableStateOf(task?.content ?: "") }
    var date by remember { mutableLongStateOf(task?.date ?: 0L) }
    var completed by remember { mutableStateOf(task?.completed ?: false) }

    val initialDate =
        if (date == 0L) LocalDate.now()
        else DateUtils.fromTimestamp(date)

    var selectedDate by remember { mutableStateOf(initialDate) }

    var showDateDialog by remember { mutableStateOf(false) }
    val displayText =
        if (date == 0L) stringResource(R.string.date_input_label)
        else DateUtils.formatReadable(DateUtils.fromTimestamp(date))

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text =
                    if (isEditing) stringResource(R.string.edit_task)
                    else stringResource(R.string.new_task_title),
                color = colorText
            )
        },

        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.title_task)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (errors.containsKey("title")) {
                    Text(
                        text = errors["title"] ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                    )
                }
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth()

                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = completed,
                        onCheckedChange = { completed = it }
                    )
                    Text(text = stringResource(R.string.completed))
                }
                Text(
                    text = displayText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDateDialog = true }
                )

                if (showDateDialog) {
                    CalendarDialog(
                        selectedDate = selectedDate,
                        onDateSelected = { timestamp ->
                            date = timestamp
                            selectedDate = DateUtils.fromTimestamp(timestamp)
                            showDateDialog = false
                        },
                        onDismiss = { showDateDialog = false }
                    )
                }
            }

        },

        confirmButton = {
            Button(onClick = { onConfirm(
                TaskFormState(
                    title = title,
                    content = content,
                    date = date,
                    completed = completed
                )
            ) }) {
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
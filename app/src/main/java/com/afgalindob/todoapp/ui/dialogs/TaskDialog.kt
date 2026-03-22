package com.afgalindob.todoapp.ui.dialogs

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.sp

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
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                // Campo título
                var isFocused by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {

                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge, // Asegura que el texto escrito se vea bien
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            // Importante: eliminamos paddings internos ocultos si fuera necesario
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                            }
                    )

                    // 2. Placeholder CENTRADO (Visible solo cuando está vacío y SIN FOCO)
                    // Usamos AnimatedVisibility normal (asegúrate de que el import sea androidx.compose.animation.AnimatedVisibility)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = title.isEmpty() && !isFocused,
                        enter = fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 100)), // Espera a que el pequeño se vaya
                        exit = fadeOut(animationSpec = tween(durationMillis = 200)) // Se va rápido
                    ) {
                        Text(
                            text = stringResource(R.string.title_task),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp) // Alineado con el cursor del TextField
                        )
                    }

                    // 3. Label PEQUEÑO ARRIBA (Visible cuando hay FOCO o TEXTO)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isFocused || title.isNotEmpty(),
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 400,
                                delayMillis = 150  // <--- AQUÍ ESTÁ EL DELAY que pedías
                            )
                        ),
                        exit = fadeOut(animationSpec = tween(300)),
                        modifier = Modifier.align(Alignment.TopStart) // Lo anclamos arriba
                    ) {
                        Text(
                            text = stringResource(R.string.title_task),
                            color = if (isFocused) MaterialTheme.colorScheme.primary else Color.Gray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 0.dp)
                        )
                    }
                }

                // Error título
                if (errors.containsKey("title")) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errors["title"] ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp)) // espacio entre campos

                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo contenido
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    modifier =
                        Modifier.fillMaxWidth()
                            .heightIn(min = 200.dp)
                )

                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Checkbox completado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = completed,
                        onCheckedChange = { completed = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.completed))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Selector de fecha
                Text(
                    text = displayText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDateDialog = true }
                        .padding(vertical = 8.dp)
                )

                // Dialogo calendario
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

                Spacer(modifier = Modifier.height(8.dp)) // espacio final para no quedar pegado al confirmButton
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
package com.afgalindob.todoapp.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.afgalindob.todoapp.ui.animations.AnimatedPlaceholder
import com.afgalindob.todoapp.ui.theme.AccentPrimary
import com.afgalindob.todoapp.ui.theme.AccentSecondary
import com.afgalindob.todoapp.ui.theme.ErrorColor
import com.afgalindob.todoapp.ui.theme.OnAccentSecondary
import com.afgalindob.todoapp.ui.theme.OnSurfacePrimary
import com.afgalindob.todoapp.ui.theme.OnSurfaceSecondary
import com.afgalindob.todoapp.ui.theme.SurfaceContainer
import com.afgalindob.todoapp.ui.theme.SurfaceVariant

@Composable 
fun TaskDialog(
    task: TaskDomain? = null,
    errors: Map<String,String> = emptyMap(),
    onConfirm: (TaskFormState) -> Unit,
    onDismiss: () -> Unit
) {

    var title by remember { mutableStateOf(task?.title ?: "") }
    var content by remember { mutableStateOf(task?.content ?: "") }
    var date by remember { mutableLongStateOf(task?.date ?: 0L) }
    var completed by remember { mutableStateOf(task?.completed ?: false) }

    var isTitleFocused by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }

    val transparentFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )

    val initialDate =
        if (date == 0L) LocalDate.now()
        else DateUtils.fromTimestamp(date)
    var selectedDate by remember { mutableStateOf(initialDate) }

    AlertDialog(
        onDismissRequest = onDismiss,

        containerColor = SurfaceContainer,

        title = {
            Text(
                text =
                    stringResource(
                        if (task != null)
                            R.string.edit_task
                        else
                            R.string.new_task_title
                    ),
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        },

        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                // --- SECCIÓN TÍTULO ---
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {

                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = transparentFieldColors,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                isTitleFocused = it.isFocused
                            }
                    )

                    // Animaciones extraídas
                    AnimatedPlaceholder(
                        text = stringResource(R.string.title_task),
                        isVisible = title.isEmpty() && !isTitleFocused,
                        isLabel = false
                    )

                    Box(Modifier.align(Alignment.TopStart)) {
                        AnimatedPlaceholder(
                            text = stringResource(R.string.title_task),
                            isVisible = isTitleFocused || title.isNotEmpty(),
                            isLabel = true,
                            isFocused = isTitleFocused
                        )
                    }
                }

                // Error título
                if (errors.containsKey("title")) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errors["title"] ?: "",
                        color = ErrorColor,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- SECCIÓN CONTENIDO ---
                HorizontalDivider(
                    color = SurfaceVariant,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = content,
                    onValueChange = { content = it },
                    colors = transparentFieldColors,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp)
                )

                HorizontalDivider(
                    color = SurfaceVariant,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- FECHA ---
                Text(
                    text =
                        if (date == 0L)
                            stringResource(R.string.date_input_label)
                        else
                            DateUtils.formatReadable(DateUtils.fromTimestamp(date)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (date == 0L) OnSurfaceSecondary
                            else OnSurfacePrimary,
                    textAlign = TextAlign.Center,
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

                Spacer(modifier = Modifier.height(12.dp))

                // Checkbox completado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = completed,
                        onCheckedChange = { completed = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AccentPrimary,
                            uncheckedColor = SurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.completed),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            }
        },

        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Botón cancelar
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceVariant.copy(alpha = 0.3f),
                        contentColor = OnSurfacePrimary
                    ),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                // Boton Confirm
                Button(
                    onClick = {
                        onConfirm(
                            TaskFormState(
                                title = title,
                                content = content,
                                date = date,
                                completed = completed
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentSecondary,
                        contentColor = OnAccentSecondary
                    ),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(
                            if (task != null)
                                R.string.apply
                            else
                                R.string.add_task
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },

        dismissButton = {}
    )
}
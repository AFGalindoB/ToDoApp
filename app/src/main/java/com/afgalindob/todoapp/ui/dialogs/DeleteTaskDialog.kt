package com.afgalindob.todoapp.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.domain.TaskDomain
import com.afgalindob.todoapp.ui.theme.AccentSecondary
import com.afgalindob.todoapp.ui.theme.OnAccentSecondary
import com.afgalindob.todoapp.ui.theme.OnSurfacePrimary
import com.afgalindob.todoapp.ui.theme.OnSurfaceSecondary
import com.afgalindob.todoapp.ui.theme.SurfaceContainer
import com.afgalindob.todoapp.ui.theme.SurfaceVariant

@Composable
fun DeleteTaskDialog(
    task: TaskDomain,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    val title = task.title

    AlertDialog(
        onDismissRequest = onDismiss,

        containerColor = SurfaceContainer,

        title = { Text(
            stringResource(R.string.delete_task),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
            )
        },

        text = {
            Column {
                Text(
                    stringResource(R.string.delete_task_dialog),
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(
                    color = SurfaceVariant.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(
                    color = SurfaceVariant.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // BOTÓN CANCELAR
                Button(
                    onClick = onDismiss,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
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

                // BOTÓN ELIMINAR (Confirmar)
                Button(
                    onClick = onConfirm,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = AccentSecondary,
                        contentColor = OnAccentSecondary
                    ),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text(
                        stringResource(R.string.delete_option),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        dismissButton = {}
    )
}
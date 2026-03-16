package com.afgalindob.todoapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.data.local.db.Converters
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.schema.TaskSchema

@Composable
fun TaskCard(
    task: TaskEntity,
    onToggleCompleted: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    val values = Converters().toMap(task.values)

    val title = values["title"] ?: ""
    val description = values["description"] ?: ""
    val completed = values["completed"]?.toBoolean() ?: false
    val dateField = TaskSchema.fields.first { it.key == "date" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = completed,
                    onCheckedChange = {
                        onToggleCompleted(it)
                    }
                )

                Spacer(Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    dateField.type.RenderDisplay(dateField, values, MaterialTheme.typography.bodySmall)
                }

                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.expand_more),
                        contentDescription = "Expand"
                    )
                }
            }

            AnimatedVisibility(expanded) {

                Column {

                    Spacer(Modifier.height(10.dp))

                    Text(description)

                    Spacer(Modifier.height(10.dp))

                    Column {

                        Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ){
                                Icon(
                                    painter = painterResource(R.drawable.edit),
                                    contentDescription = "Edit Task"
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(stringResource(R.string.edit_task))
                            }
                        }

                        Spacer(Modifier.height(5.dp))

                        Button(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ){
                                Icon(
                                    painter = painterResource(R.drawable.delete),
                                    contentDescription = "Delete Task"
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(stringResource(R.string.delete_task))
                            }
                        }
                    }
                }
            }
        }
    }
}
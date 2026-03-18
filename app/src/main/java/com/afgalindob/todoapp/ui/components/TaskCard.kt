package com.afgalindob.todoapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.schema.TaskSchema

@Composable
fun TaskCard(
    task: TaskEntity,
    textColor: Color,
    onToggleCompleted: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    val title = task.title
    val description = task.description
    val completed = task.completed

    val values = mapOf("date" to task.date)
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
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                    )

                    dateField.type.RenderDisplay(
                        dateField,
                        values,
                        MaterialTheme.typography.bodySmall,
                        colorText = textColor
                    )
                }

                IconButton( onClick = {expanded = !expanded} ) {
                    Icon(
                        painter = painterResource(R.drawable.expand_more),
                        contentDescription = "Expand"
                    )
                }
            }

            AnimatedVisibility(expanded) {

                Column {

                    Spacer(Modifier.height(10.dp))

                    Row (modifier = Modifier.fillMaxWidth()){
                        Text(
                            description, color = textColor,
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        )

                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.clip(CircleShape).background(Color.LightGray)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.edit),
                                contentDescription = "Edit Task",
                                tint = Color.Black
                            )
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
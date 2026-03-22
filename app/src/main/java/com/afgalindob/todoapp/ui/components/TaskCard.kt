package com.afgalindob.todoapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.afgalindob.todoapp.domain.TaskDomain
import com.afgalindob.todoapp.utils.DateUtils

@Composable
fun TaskCard(
    task: TaskDomain,
    textColor: Color,
    onToggleCompleted: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {

                Checkbox(
                    checked = task.completed,
                    onCheckedChange = { onToggleCompleted(it) }
                )

                Spacer(Modifier.width(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { expanded = !expanded }
                        .padding(8.dp) // respiración interna
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {

                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = textColor,
                            maxLines = if (expanded) Int.MAX_VALUE else 1,
                            overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                        )

                        task.date?.let {
                            Text(
                                text = DateUtils.formatReadable(DateUtils.fromTimestamp(it)),
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor
                            )
                        }
                    }

                    Spacer(Modifier.width(5.dp))

                    Icon(
                        painter = painterResource(R.drawable.expand_more),
                        contentDescription = "Expand",
                        modifier = Modifier.padding(5.dp)
                    )

                }
            }

            // Content
            AnimatedVisibility(expanded) {

                Column {

                    Spacer(Modifier.height(10.dp))

                    Row (modifier = Modifier.fillMaxWidth()){
                        Text(
                            text = task.content,
                            color = textColor,
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
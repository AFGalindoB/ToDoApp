package com.afgalindob.todoapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.domain.TaskDomain
import com.afgalindob.todoapp.utils.DateUtils
import kotlin.math.roundToInt

@Composable
fun TaskCard(
    task: TaskDomain,
    textColor: Color,
    onToggleCompleted: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(durationMillis = 100)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX > 200f) { // umbral para acción
                            onDelete()
                        }
                        offsetX = 0f // regresamos a la posición inicial
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX += dragAmount
                        if (offsetX < 0f) offsetX = 0f // no permitir deslizar hacia la izquierda
                    }
                )
            }
    )  {
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
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                                    .then(
                                        if (task.content.isEmpty())
                                            Modifier.heightIn(min = 120.dp)
                                        else Modifier
                                    )
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
                    }
                }
            }
        }
    }
}
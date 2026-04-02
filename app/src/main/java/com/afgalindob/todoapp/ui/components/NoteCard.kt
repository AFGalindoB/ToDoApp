package com.afgalindob.todoapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.domain.NoteDomain
import com.afgalindob.todoapp.ui.theme.AccentPrimary
import com.afgalindob.todoapp.ui.theme.AccentSecondary
import com.afgalindob.todoapp.ui.theme.OnAccentSecondary
import com.afgalindob.todoapp.ui.theme.OnSurfaceSecondary
import com.afgalindob.todoapp.ui.theme.SurfaceContainer

@Composable
fun NoteCard(
    note: NoteDomain,
    expanded: Boolean,
    anyCardExpanded: Boolean,
    onExpand: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }

    val borderWidth by animateDpAsState(
        targetValue = if (expanded) 2.dp else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "BorderAnimation"
    )

    val scale by animateFloatAsState(
        targetValue = when {
            expanded -> 1.0f
            anyCardExpanded -> 0.95f
            else -> 1.0f
        },
        animationSpec = tween(durationMillis = 300),
        label = "ScaleAnimation"
    )

    val alpha by animateFloatAsState(
        targetValue = when {
            expanded -> 1.0f
            anyCardExpanded -> 0.7f
            else -> 1.0f
        },
        animationSpec = tween(durationMillis = 300),
        label = "AlphaAnimation"
    )

    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) -180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "RotationAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                transformOrigin = TransformOrigin.Center,
                alpha = alpha,
                translationX = offsetX,
                clip = true
            )
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX > 200f) { // umbral para acción
                            onDelete()
                        }
                        offsetX = 0f // regresamos a la posición inicial
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceAtLeast(0f)
                    }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            border = if (expanded) BorderStroke(borderWidth, AccentPrimary) else null,
            colors = CardDefaults.cardColors(
                containerColor = SurfaceContainer,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (expanded) 8.dp else 2.dp
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // --- HEADER ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) { onExpand() }
                        .padding(8.dp)
                ) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f).padding(5.dp),
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.width(5.dp))

                    Icon(
                        painter = painterResource(R.drawable.expand_more),
                        contentDescription = "Expand",
                        tint = OnSurfaceSecondary,
                        modifier = Modifier
                            .padding(5.dp)
                            .graphicsLayer(rotationZ = rotationAngle)
                    )
                }

                // -- CONTENT ---
                AnimatedVisibility(expanded) {
                    Column {
                        Spacer(Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp)
                        ) {
                            Text(
                                text = note.content,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 48.dp)
                            )

                            IconButton(
                                onClick = onEdit,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(AccentSecondary)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.edit),
                                    contentDescription = "Edit Note",
                                    tint = OnAccentSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
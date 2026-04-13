package com.afgalindob.todoapp.ui.components.cards

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.afgalindob.todoapp.domain.TaskDomain
import com.afgalindob.todoapp.ui.components.BaseCard
import com.afgalindob.todoapp.ui.components.CardEvent
import com.afgalindob.todoapp.ui.theme.AccentPrimary
import com.afgalindob.todoapp.ui.theme.OnSurfaceSecondary
import com.afgalindob.todoapp.ui.theme.SurfaceVariant
import com.afgalindob.todoapp.utils.DateUtils
import com.afgalindob.todoapp.R

sealed interface TaskEvent {
    data class ToggleCompleted(val completed: Boolean) : TaskEvent
    object Edit : TaskEvent
    object Delete : TaskEvent
}

@Composable
fun TaskCard(
    task: TaskDomain,
    date: Long? = null,
    onTrash: Boolean = false,
    expanded: Boolean,
    anyCardExpanded: Boolean,
    onExpand: () -> Unit,
    enableSwipe: Boolean = true,
    onEvent: (TaskEvent) -> Unit,
    actionArea: @Composable BoxScope.() -> Unit
) {
    BaseCard(
        expanded = expanded,
        anyCardExpanded = anyCardExpanded,
        onExpand = onExpand,
        enableSwipe = enableSwipe,
        onEvent = { event ->
            if (event is CardEvent.Swipe) onEvent(TaskEvent.Delete)
        },
        headerPrefix = {
            Checkbox(
                checked = task.completed,
                onCheckedChange = { onEvent(TaskEvent.ToggleCompleted(it)) },
                colors = CheckboxDefaults.colors(
                    checkedColor = AccentPrimary,
                    uncheckedColor = SurfaceVariant
                )
            )
        },
        titleArea = {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                )

                date?.let {
                    Text(
                        text = if (onTrash)
                            stringResource(R.string.will_be_deleted) + " " + DateUtils.formatReadable(DateUtils.fromTimestamp(it))
                        else
                            DateUtils.formatReadable(DateUtils.fromTimestamp(it)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceSecondary,
                    )
                }
            }
        },
        expandedContent = {
            Text(
                text = task.content,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 48.dp)
            )
        },
        actionArea = actionArea
    )
}
package com.afgalindob.assistantapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.afgalindob.assistantapp.R
import com.afgalindob.assistantapp.domain.NoteDomain
import com.afgalindob.assistantapp.domain.TaskDomain
import com.afgalindob.assistantapp.navigation.DialogType
import com.afgalindob.assistantapp.ui.components.EntitySnackbar
import com.afgalindob.assistantapp.ui.components.SectionHeader
import com.afgalindob.assistantapp.ui.components.cards.NoteCard
import com.afgalindob.assistantapp.ui.components.cards.TaskCard
import com.afgalindob.assistantapp.ui.dialogs.DeleteEntityDialog
import com.afgalindob.assistantapp.ui.theme.AccentSecondary
import com.afgalindob.assistantapp.ui.theme.BackgroundColor
import com.afgalindob.assistantapp.ui.theme.OnAccentSecondary
import com.afgalindob.assistantapp.viewmodel.room.TrashViewModel

sealed class SelectedEntity {
    data class Task(val task: TaskDomain) : SelectedEntity()
    data class Note(val note: NoteDomain) : SelectedEntity()
}

@Composable
fun TrashScreen(
    viewModel: TrashViewModel,
    onRendered: () -> Unit
) {
    val tasks by viewModel.deletedTasks.collectAsState()
    val notes by viewModel.deletedNotes.collectAsState()

    var expandedEntity by remember { mutableStateOf<SelectedEntity?>(null) }
    val isAnyExpanded by remember { derivedStateOf { expandedEntity != null } }

    var lastRestoredEntity by remember { mutableStateOf<SelectedEntity?>(null) }
    var deletingEntity by remember { mutableStateOf<SelectedEntity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        repeat(2) { withFrameNanos { } }
        onRendered()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = BackgroundColor) {
        Box(modifier = Modifier.fillMaxSize()){
            LazyColumn(modifier = Modifier.fillMaxSize()){

                if (tasks.isEmpty() && notes.isEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.trash_placeholder),
                            style = MaterialTheme.typography.displayLarge,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // --- SECCIÓN DE TAREAS ---
                if (tasks.isNotEmpty()) {
                    item { SectionHeader(R.string.tasks) } // Header
                    items(tasks, key = { "task_${it.id}" }) { task ->
                        val isExpanded = expandedEntity is SelectedEntity.Task &&
                                (expandedEntity as SelectedEntity.Task).task.id == task.id

                        TaskCard(
                            task = task,
                            expanded = isExpanded,
                            date = task.deleteAt,
                            onTrash = true,
                            anyCardExpanded = isAnyExpanded,
                            enableSwipe = false,
                            onExpand = {
                                expandedEntity = if (isExpanded) null else SelectedEntity.Task(task)
                            },
                            onEvent = { },
                            actionArea = {
                                Column (
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ){
                                    IconButton(
                                        onClick = {
                                            viewModel.restoreTask(task)
                                            expandedEntity = null
                                            lastRestoredEntity = SelectedEntity.Task(task)
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(AccentSecondary)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.restore),
                                            contentDescription = "Restore Task",
                                            tint = OnAccentSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(15.dp))
                                    IconButton(
                                        onClick = {
                                            deletingEntity = SelectedEntity.Task(task)
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(AccentSecondary)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.trash),
                                            contentDescription = "Delete Task",
                                            tint = OnAccentSecondary
                                        )
                                    }
                                }
                            }
                        )
                    }
                }

                // --- SECCIÓN DE NOTAS ---
                if (notes.isNotEmpty()) {
                    item { SectionHeader(R.string.notes) }
                    items(notes, key = { "note_${it.id}" }) { note ->
                        val isExpanded = expandedEntity is SelectedEntity.Note &&
                                (expandedEntity as SelectedEntity.Note).note.id == note.id

                        NoteCard(
                            note = note,
                            date = note.deleteAt,
                            onTrash = true,
                            expanded = isExpanded,
                            anyCardExpanded = isAnyExpanded,
                            enableSwipe = false,
                            onExpand = {
                                expandedEntity = if (isExpanded) null else SelectedEntity.Note(note)
                            },
                            onEvent = { },
                            actionArea = {
                                Column(
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    IconButton(
                                        onClick = {
                                            viewModel.restoreNote(note)
                                            expandedEntity = null
                                            lastRestoredEntity = SelectedEntity.Note(note)
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(AccentSecondary)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.restore),
                                            contentDescription = "Restore Note",
                                            tint = OnAccentSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(15.dp))
                                    IconButton(
                                        onClick = {
                                            deletingEntity = SelectedEntity.Note(note)
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(AccentSecondary)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.trash),
                                            contentDescription = "Delete Note",
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
            EntitySnackbar(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
    val undoLabel = stringResource(R.string.undo)
    val taskRestoredMessage = stringResource(R.string.task) + " " + stringResource(R.string.restored)
    val noteRestoredMessage = stringResource(R.string.note) + " " + stringResource(R.string.restored)
    LaunchedEffect(lastRestoredEntity) {
        val entity = lastRestoredEntity

        if (entity != null) {
            val message = when(entity) {
                is SelectedEntity.Task -> taskRestoredMessage
                is SelectedEntity.Note -> noteRestoredMessage
            }

            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                when(entity) {
                    is SelectedEntity.Task -> viewModel.reDeleteTask(entity.task.id, entity.task.deleteAt?: 0)
                    is SelectedEntity.Note -> viewModel.reDeleteNote(entity.note.id, entity.note.deleteAt?: 0)
                }
            }

            lastRestoredEntity = null
        }
    }
    deletingEntity?.let { entity ->
        DeleteEntityDialog(
            title = when(entity) {
                is SelectedEntity.Task -> entity.task.title
                is SelectedEntity.Note -> entity.note.title
            },
            type = if (entity is SelectedEntity.Task) DialogType.TASK else DialogType.NOTE,
            onConfirm = {
                when(entity) {
                    is SelectedEntity.Task -> viewModel.deletePermanentTask(entity.task)
                    is SelectedEntity.Note -> viewModel.deletePermanentNote(entity.note)
                }
                deletingEntity = null
                expandedEntity = null
            },
            onDismiss = { deletingEntity = null }
        )
    }
}
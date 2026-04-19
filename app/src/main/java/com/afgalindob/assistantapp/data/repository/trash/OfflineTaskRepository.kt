package com.afgalindob.assistantapp.data.repository.trash

import com.afgalindob.assistantapp.data.repository.note.NoteRepository
import com.afgalindob.assistantapp.data.repository.task.TaskRepository
import kotlinx.coroutines.flow.Flow
import com.afgalindob.assistantapp.data.local.entity.NoteEntity
import com.afgalindob.assistantapp.data.local.entity.TaskEntity

class OfflineTrashRepository(
    private val taskRepository: TaskRepository,
    private val noteRepository: NoteRepository
) : TrashRepository {

    override fun getDeletedTasks(): Flow<List<TaskEntity>> =
        taskRepository.getDeletedTasks()

    override fun getDeletedNotes(): Flow<List<NoteEntity>> =
        noteRepository.getDeletedNotes()

    override suspend fun restoreTask(id: Long) =
        taskRepository.restoreTask(id)

    override suspend fun restoreNote(id: Long) =
        noteRepository.restoreNote(id)

    override suspend fun reDeleteTask(id: Long, days: Long) =
        taskRepository.restoreSetOnDeleteTask(id, days)

    override suspend fun reDeleteNote(id: Long, days: Long) =
        noteRepository.restoreSetOnDeleteNote(id, days)

    override suspend fun permanentlyDeleteTask(id: Long) =
        taskRepository.deleteTaskById(id)

    override suspend fun permanentlyDeleteNote(id: Long) =
        noteRepository.deleteNoteById(id)
}
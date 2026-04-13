package com.afgalindob.todoapp.data.repository.trash

import com.afgalindob.todoapp.data.local.entity.NoteEntity
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TrashRepository {
    fun getDeletedTasks(): Flow<List<TaskEntity>>
    fun getDeletedNotes(): Flow<List<NoteEntity>>

    suspend fun restoreTask(id: Long)
    suspend fun restoreNote(id: Long)

    suspend fun reDeleteTask(id: Long, days: Long)
    suspend fun reDeleteNote(id: Long, days: Long)

    suspend fun permanentlyDeleteTask(id: Long)
    suspend fun permanentlyDeleteNote(id: Long)
}
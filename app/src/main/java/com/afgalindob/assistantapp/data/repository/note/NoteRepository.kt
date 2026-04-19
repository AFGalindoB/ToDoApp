package com.afgalindob.assistantapp.data.repository.note

import com.afgalindob.assistantapp.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getNotes(): Flow<List<NoteEntity>>

    fun getDeletedNotes(): Flow<List<NoteEntity>>

    suspend fun insertNote(note: NoteEntity): Long

    suspend fun deleteNoteById(id: Long)

    suspend fun updateNote(note: NoteEntity)

    suspend fun setOnDeleteNote(id: Long, days: Long)

    suspend fun restoreSetOnDeleteNote(id: Long, days: Long)

    suspend fun restoreNote(id: Long)

    suspend fun deleteExpiredNotes(now: Long)
}


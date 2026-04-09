package com.afgalindob.todoapp.data.repository.note

import com.afgalindob.todoapp.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getNotes(): Flow<List<NoteEntity>>

    suspend fun insertNote(note: NoteEntity): Long

    suspend fun deleteNoteById(id: Long)

    suspend fun updateNote(note: NoteEntity)

    suspend fun setOnDeleteNote(id: Long, days: Long)

}


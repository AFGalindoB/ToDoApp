package com.afgalindob.todoapp.data.repository.note

import com.afgalindob.todoapp.data.local.dao.NoteDao
import com.afgalindob.todoapp.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

class OfflineNoteRepository(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getNotes(): Flow<List<NoteEntity>> =
        noteDao.getNotes()

    override suspend fun insertNote(note: NoteEntity) =
        noteDao.insertNote(note)

    override suspend fun deleteNoteById(id: Long) =
        noteDao.deleteNoteById(id)

    override suspend fun updateNote(note: NoteEntity) =
        noteDao.updateNote(note)

}
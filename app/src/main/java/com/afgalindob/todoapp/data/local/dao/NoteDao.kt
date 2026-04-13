package com.afgalindob.todoapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.afgalindob.todoapp.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE deleteAt = 0 ORDER BY createdAt ASC")
    fun getNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE deleteAt > 0 ORDER BY deleteAt ASC")
    fun getDeletedNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("UPDATE notes SET deleteAt = :timestamp WHERE id = :id")
    suspend fun setOnDeleteNote(id: Long, timestamp: Long)

    @Query("UPDATE notes SET deleteAt = 0 WHERE id = :id")
    suspend fun restoreNote(id: Long)

    @Query("DELETE FROM notes WHERE deleteAt != 0 AND deleteAt < :now")
    suspend fun deleteExpiredNotes(now: Long)
}
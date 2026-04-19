package com.afgalindob.assistantapp.data.container

import android.content.Context
import com.afgalindob.assistantapp.data.local.db.AppDatabase
import com.afgalindob.assistantapp.data.repository.note.NoteRepository
import com.afgalindob.assistantapp.data.repository.note.OfflineNoteRepository
import com.afgalindob.assistantapp.data.repository.task.OfflineTaskRepository
import com.afgalindob.assistantapp.data.repository.task.TaskRepository
import com.afgalindob.assistantapp.data.repository.trash.OfflineTrashRepository
import com.afgalindob.assistantapp.data.repository.trash.TrashRepository

class AppDataContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }

    override val taskRepository: TaskRepository by lazy {
        OfflineTaskRepository(database.taskDao())
    }

    override val noteRepository: NoteRepository by lazy {
        OfflineNoteRepository(database.noteDao())
    }

    override val trashRepository: TrashRepository by lazy {
        OfflineTrashRepository(taskRepository, noteRepository)
    }
}
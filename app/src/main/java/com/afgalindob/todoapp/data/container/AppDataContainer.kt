package com.afgalindob.todoapp.data.container

import android.content.Context
import com.afgalindob.todoapp.data.local.db.AppDatabase
import com.afgalindob.todoapp.data.repository.note.NoteRepository
import com.afgalindob.todoapp.data.repository.note.OfflineNoteRepository
import com.afgalindob.todoapp.data.repository.task.OfflineTaskRepository
import com.afgalindob.todoapp.data.repository.task.TaskRepository

class AppDataContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }

    override val taskRepository: TaskRepository by lazy {
        OfflineTaskRepository(database.taskDao())
    }

    override val noteRepository: NoteRepository by lazy {
        OfflineNoteRepository(database.noteDao())
    }
}
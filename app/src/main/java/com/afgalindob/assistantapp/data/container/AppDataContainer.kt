package com.afgalindob.assistantapp.data.container

import android.content.Context
import com.afgalindob.assistantapp.data.local.db.AppDatabase
import com.afgalindob.assistantapp.data.local.preferences.UserPreferencesManager
import com.afgalindob.assistantapp.data.repository.note.NoteRepository
import com.afgalindob.assistantapp.data.repository.note.OfflineNoteRepository
import com.afgalindob.assistantapp.data.repository.task.OfflineTaskRepository
import com.afgalindob.assistantapp.data.repository.task.TaskRepository
import com.afgalindob.assistantapp.data.repository.trash.OfflineTrashRepository
import com.afgalindob.assistantapp.data.repository.trash.TrashRepository
import com.afgalindob.assistantapp.data.repository.user.OfflineUserRepository
import com.afgalindob.assistantapp.data.repository.user.UserRepository

class AppDataContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }

    private val userPreferencesManager: UserPreferencesManager by lazy {
        UserPreferencesManager(context)
    }

    override val taskRepository: TaskRepository by lazy {
        OfflineTaskRepository(database.taskDao())
    }

    override val noteRepository: NoteRepository by lazy {
        OfflineNoteRepository(database.noteDao())
    }

    override val trashRepository: TrashRepository by lazy {
        OfflineTrashRepository(taskRepository, noteRepository)
    }

    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(userPreferencesManager)
    }
}
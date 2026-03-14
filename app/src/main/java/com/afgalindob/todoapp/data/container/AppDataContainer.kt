package com.afgalindob.todoapp.data.container

import android.content.Context
import com.afgalindob.todoapp.data.local.db.AppDatabase
import com.afgalindob.todoapp.data.repository.OfflineTaskRepository
import com.afgalindob.todoapp.data.repository.TaskRepository

class AppDataContainer(private val context: Context) : AppContainer {

    override val taskRepository: TaskRepository by lazy {

        OfflineTaskRepository(
            AppDatabase
                .getDatabase(context)
                .taskDao()
        )
    }
}
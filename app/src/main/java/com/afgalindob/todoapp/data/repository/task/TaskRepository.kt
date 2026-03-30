package com.afgalindob.todoapp.data.repository.task

import com.afgalindob.todoapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTasks(showCompleted: Boolean, today: Long): Flow<List<TaskEntity>>

    suspend fun insertTask(task: TaskEntity): Long

    suspend fun deleteTaskById(id: Long)

    suspend fun updateTask(task: TaskEntity)

}
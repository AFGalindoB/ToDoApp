package com.afgalindob.todoapp.data.repository

import com.afgalindob.todoapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getAllTasksStream(): Flow<List<TaskEntity>>

    fun getPendingTasks(): Flow<List<TaskEntity>>

    fun getTasksByDate(): Flow<List<TaskEntity>>

    suspend fun insertTask(task: TaskEntity): Long

    suspend fun deleteTaskById(id: Long)

    suspend fun updateTask(task: TaskEntity)

}
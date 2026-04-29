package com.afgalindob.assistantapp.data.repository.task

import com.afgalindob.assistantapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTasks(showCompleted: Boolean, today: Long): Flow<List<TaskEntity>>

    fun getDeletedTasks(): Flow<List<TaskEntity>>

    suspend fun getPendingTasksForToday(endOfDay: Long): List<TaskEntity>

    suspend fun insertTask(task: TaskEntity): Long

    suspend fun deleteTaskById(id: Long)

    suspend fun updateTask(task: TaskEntity)

    suspend fun setOnDeleteTask(id: Long, days: Long)

    suspend fun restoreSetOnDeleteTask(id: Long, days: Long)

    suspend fun restoreTask(id: Long)

    suspend fun deleteExpiredTasks(now: Long)
}
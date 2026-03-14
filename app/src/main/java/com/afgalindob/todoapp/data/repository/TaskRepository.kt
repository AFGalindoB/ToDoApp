package com.afgalindob.todoapp.data.repository

import com.afgalindob.todoapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getAllTasksStream(): Flow<List<TaskEntity>>

    fun getTaskStream(id: Int): Flow<TaskEntity?>

    suspend fun insertTask(task: TaskEntity)

    suspend fun deleteTask(task: TaskEntity)

    suspend fun updateTask(task: TaskEntity)

}
package com.afgalindob.todoapp.data.repository

import com.afgalindob.todoapp.data.local.dao.TaskDao
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class OfflineTaskRepository(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasksStream(): Flow<List<TaskEntity>> =
        taskDao.getAllTasks()

    override fun getPendingTasks(): Flow<List<TaskEntity>> =
        taskDao.getPendingTasks()

    override fun getTasksByDate(): Flow<List<TaskEntity>> =
        taskDao.getTasksByDate()

    override suspend fun insertTask(task: TaskEntity) =
        taskDao.insert(task)

    override suspend fun deleteTaskById(id: Long) =
        taskDao.deleteTaskById(id)

    override suspend fun updateTask(task: TaskEntity) =
        taskDao.update(task)

}
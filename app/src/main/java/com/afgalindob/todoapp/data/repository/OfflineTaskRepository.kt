package com.afgalindob.todoapp.data.repository

import com.afgalindob.todoapp.data.local.dao.TaskDao
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class OfflineTaskRepository(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasksStream(): Flow<List<TaskEntity>> =
        taskDao.getAllTasks()

    override fun getTaskStream(id: Int): Flow<TaskEntity?> =
        taskDao.getTask(id)

    override suspend fun insertTask(task: TaskEntity) =
        taskDao.insert(task)

    override suspend fun deleteTask(task: TaskEntity) =
        taskDao.delete(task)

    override suspend fun updateTask(task: TaskEntity) =
        taskDao.update(task)

}
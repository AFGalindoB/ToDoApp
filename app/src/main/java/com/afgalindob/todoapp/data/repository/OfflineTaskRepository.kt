package com.afgalindob.todoapp.data.repository

import com.afgalindob.todoapp.data.local.dao.TaskDao
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class OfflineTaskRepository(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasks(showCompleted: Boolean, today: Long): Flow<List<TaskEntity>> =
        taskDao.getTasks(showCompleted, today)

    override suspend fun insertTask(task: TaskEntity) =
        taskDao.insert(task)

    override suspend fun deleteTaskById(id: Long) =
        taskDao.deleteTaskById(id)

    override suspend fun updateTask(task: TaskEntity) =
        taskDao.update(task)

}
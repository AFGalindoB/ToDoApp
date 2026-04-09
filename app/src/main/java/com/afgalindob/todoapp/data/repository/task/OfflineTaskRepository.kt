package com.afgalindob.todoapp.data.repository.task

import com.afgalindob.todoapp.data.local.dao.TaskDao
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.utils.DateUtils
import kotlinx.coroutines.flow.Flow

class OfflineTaskRepository(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasks(showCompleted: Boolean, today: Long): Flow<List<TaskEntity>> =
        taskDao.getTasks(showCompleted, today)

    override suspend fun insertTask(task: TaskEntity) =
        taskDao.insertTask(task)

    override suspend fun deleteTaskById(id: Long) =
        taskDao.deleteTaskById(id)

    override suspend fun updateTask(task: TaskEntity) =
        taskDao.updateTask(task)

    override suspend fun setOnDeleteTask(id: Long, days: Long) {
        val expirationTimestamp = DateUtils.getExpirationTimestamp(days)
        taskDao.setOnDeleteTask(id, expirationTimestamp)
    }
}
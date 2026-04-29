package com.afgalindob.assistantapp.data.repository.task

import com.afgalindob.assistantapp.data.local.dao.TaskDao
import com.afgalindob.assistantapp.data.local.entity.TaskEntity
import com.afgalindob.assistantapp.utils.DateUtils
import kotlinx.coroutines.flow.Flow

class OfflineTaskRepository(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasks(showCompleted: Boolean, today: Long): Flow<List<TaskEntity>> =
        taskDao.getTasks(showCompleted, today)

    override fun getDeletedTasks(): Flow<List<TaskEntity>> =
        taskDao.getDeletedTasks()

    override suspend fun getPendingTasksForToday(endOfDay: Long): List<TaskEntity> =
        taskDao.getPendingTasksForToday(endOfDay)

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

    override suspend fun restoreSetOnDeleteTask(id: Long, days: Long) =
        taskDao.setOnDeleteTask(id, days)

    override suspend fun restoreTask(id: Long) =
        taskDao.restoreTask(id)

    override suspend fun deleteExpiredTasks(now: Long) =
        taskDao.deleteExpiredTasks(now)
}
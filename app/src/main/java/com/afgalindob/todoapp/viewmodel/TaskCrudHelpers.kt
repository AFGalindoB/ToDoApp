package com.afgalindob.todoapp.viewmodel

import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.data.repository.TaskRepository
import com.afgalindob.todoapp.domain.TaskDomain
import com.afgalindob.todoapp.domain.TaskFormState
import com.afgalindob.todoapp.utils.DateUtils

class TaskCrudHelpers(private val repository: TaskRepository) {

    suspend fun createTask(form: TaskFormState) {
        val now = DateUtils.now()
        val task = TaskEntity(
            title = form.title,
            content = form.content,
            date = form.date ?: 0L,
            completed = form.completed,
            createdAt = now,
            updatedAt = now
        )
        repository.insertTask(task)
    }

    suspend fun updateTask(task: TaskDomain, form: TaskFormState) {
        val updated = TaskEntity(
            id = task.id,
            title = form.title,
            content = form.content,
            date = form.date ?: 0L,
            completed = form.completed,
            createdAt = task.createdAt,
            updatedAt = DateUtils.now()
        )
        repository.updateTask(updated)
    }

    suspend fun deleteTask(taskId: Long) {
        repository.deleteTaskById(taskId)
    }
}
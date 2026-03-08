package com.afgalindob.todoapp.data

import androidx.compose.runtime.mutableStateListOf

object TaskRepository {
    val tasks = mutableStateListOf<Task>()

    fun addTask(task: Task) {
        tasks.add(task)
    }
    fun removeTask(task: Task) {
        tasks.removeIf({ it.id == task.id })
    }

    fun updateTask(taskId: String, newTitle: String, newDescription: String) {

        val index = tasks.indexOfFirst { it.id == taskId }

        if (index != -1) {
            tasks[index] = tasks[index].copy(
                title = newTitle,
                description = newDescription
            )
        }

    }
}
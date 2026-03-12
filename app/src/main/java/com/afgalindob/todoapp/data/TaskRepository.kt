package com.afgalindob.todoapp.data

import androidx.compose.runtime.mutableStateListOf

object TaskRepository {
    val tasks = mutableStateListOf<Task>()

    fun addTask(task: Task) {
        tasks.add(task)
    }
    fun removeTask(taskId: String) {
        tasks.removeIf { it.id == taskId }
    }

    fun updateTask(taskId: String, newValues: Map<String,String>) {

        val index = tasks.indexOfFirst { it.id == taskId }

        if (index != -1) {
            tasks[index] = tasks[index].copy(values = newValues)
        }

    }
}
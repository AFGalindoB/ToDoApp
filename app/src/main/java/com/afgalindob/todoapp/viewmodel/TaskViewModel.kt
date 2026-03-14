package com.afgalindob.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afgalindob.todoapp.data.local.db.Converters
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val converters = Converters()

    val tasks = repository
        .getAllTasksStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createTask(values: Map<String,String>) {
        viewModelScope.launch {
            val json = converters.fromMap(values)
            repository.insertTask(TaskEntity(values = json))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updateTask(task: TaskEntity, values: Map<String,String>) {
        viewModelScope.launch {
            val json = converters.fromMap(values)
            repository.updateTask(task.copy(values = json))
        }
    }
}
package com.afgalindob.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.data.repository.TaskRepository
import com.afgalindob.todoapp.schema.TaskSchema
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

enum class TaskFilter {
    ALL,
    PENDING,
    BY_DATE
}

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val filter = MutableStateFlow(TaskFilter.ALL)

    val tasks = filter.flatMapLatest { filterType ->
        when (filterType) {
            TaskFilter.ALL -> repository.getAllTasksStream()
            TaskFilter.PENDING -> repository.getPendingTasks()
            TaskFilter.BY_DATE -> repository.getTasksByDate()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(taskFilter: TaskFilter) {
        filter.value = taskFilter
    }

    fun validate(values: Map<String, String>): Map<String, String> {

        val errors = mutableMapOf<String, String>()

        TaskSchema.fields.forEach { field ->

            val value = values[field.key]

            if (field.required && value.isNullOrBlank()) {
                errors[field.key] = "Required field"
            }

            field.maxLenghtChar?.let{ max ->
                if (value != null && value.length > max){
                    errors[field.key] = "Max $max characters"
                }
            }

        }

        return errors
    }

    fun createTask(values: Map<String,String>) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val task = TaskEntity(
                title = values["title"] ?: "",
                description = values["description"] ?: "",
                date = values["date"] ?: "",
                completed = values["completed"] == "true",
                createdAt = now,
                updatedAt = now
            )

            repository.insertTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updateTask(task: TaskEntity, values: Map<String,String>) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                title = values["title"] ?: "",
                description = values["description"] ?: "",
                date = values["date"] ?: "",
                completed = values["completed"] == "true",
                updatedAt = System.currentTimeMillis()
            )
            repository.updateTask(updatedTask)
        }
    }
}
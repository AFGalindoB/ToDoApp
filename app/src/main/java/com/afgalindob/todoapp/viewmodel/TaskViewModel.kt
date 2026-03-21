package com.afgalindob.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.data.mapper.TaskMapper.toDomain
import com.afgalindob.todoapp.data.repository.TaskRepository
import com.afgalindob.todoapp.schema.TaskDomain
import com.afgalindob.todoapp.schema.TaskFormState
import com.afgalindob.todoapp.schema.TaskSchema
import com.afgalindob.todoapp.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

enum class TaskFilter {
    ALL,
    PENDING,
    BY_DATE
}

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val filter = MutableStateFlow(TaskFilter.ALL)

    // Obtener las tareas de la base de datos y aplicar el filtro
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

    // Hacer la conversion de dominios entre la base de datos y la UI
    val tasksDomain: StateFlow<List<TaskDomain>> =
        tasks
            .map { list -> list.map { it.toDomain() } }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
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

    fun createTask(form: TaskFormState) {
        viewModelScope.launch {
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
    }

    fun deleteTask(task: TaskDomain) {
        viewModelScope.launch {
            repository.deleteTaskById(task.id)
        }
    }

    fun updateTask(task: TaskDomain, form: TaskFormState) {
        viewModelScope.launch {
            val data = TaskEntity(
                id = task.id,
                title = form.title,
                content = form.content,
                date = form.date ?: 0L,
                completed = form.completed,
                createdAt = task.createdAt,
                updatedAt = DateUtils.now()
            )
                repository.updateTask(data)
        }
    }
}
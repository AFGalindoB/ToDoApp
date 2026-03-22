package com.afgalindob.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afgalindob.todoapp.data.mapper.TaskMapper.toDomain
import com.afgalindob.todoapp.data.repository.TaskRepository
import com.afgalindob.todoapp.domain.TaskDomain
import com.afgalindob.todoapp.domain.TaskFormState
import com.afgalindob.todoapp.utils.getSection
import com.afgalindob.todoapp.utils.sectionOrder
import com.afgalindob.todoapp.utils.validateTaskForm
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
    private val crudHelpers = TaskCrudHelpers(repository)

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

    val tasksBySection: StateFlow<Map<Int, List<TaskDomain>>> =
        tasksDomain.map { list ->
            list.groupBy { it.getSection() }
                .toSortedMap(compareBy { sectionOrder(it) })
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap()
        )

    fun setFilter(taskFilter: TaskFilter) {
        filter.value = taskFilter
    }

    fun validate(form: TaskFormState) = validateTaskForm(form)

    fun createTask(form: TaskFormState) {
        viewModelScope.launch {
            crudHelpers.createTask(form)
        }
    }

    fun deleteTask(task: TaskDomain) {
        viewModelScope.launch {
            crudHelpers.deleteTask(task.id)
        }
    }

    fun updateTask(task: TaskDomain, form: TaskFormState) {
        viewModelScope.launch {
            crudHelpers.updateTask(task, form)
        }
    }
}
package com.afgalindob.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.data.mapper.TaskMapper.toDomain
import com.afgalindob.todoapp.data.repository.task.TaskRepository
import com.afgalindob.todoapp.domain.TaskDomain
import com.afgalindob.todoapp.domain.TaskFormState
import com.afgalindob.todoapp.domain.validation.validateTaskForm
import com.afgalindob.todoapp.utils.DateUtils
import com.afgalindob.todoapp.utils.getSection
import com.afgalindob.todoapp.utils.sectionOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val showCompleted = MutableStateFlow(false)
    val showCompletedState: StateFlow<Boolean> = showCompleted

    val tasks = showCompleted.flatMapLatest { showCompleted ->
        repository.getTasks(showCompleted, today = DateUtils.today())
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

    fun toggleShowCompleted(showTaskCompleted: Boolean) {
        showCompleted.value = showTaskCompleted
    }

    fun validate(form: TaskFormState) = validateTaskForm(form)

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
    }
}
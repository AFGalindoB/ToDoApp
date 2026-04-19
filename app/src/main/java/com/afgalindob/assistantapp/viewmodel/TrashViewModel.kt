package com.afgalindob.assistantapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afgalindob.assistantapp.data.repository.trash.TrashRepository
import com.afgalindob.assistantapp.domain.NoteDomain
import com.afgalindob.assistantapp.domain.TaskDomain
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.afgalindob.assistantapp.data.mapper.NoteMapper.toDomain
import com.afgalindob.assistantapp.data.mapper.TaskMapper.toDomain

class TrashViewModel(private val repository: TrashRepository) : ViewModel() {

    val deletedNotes: StateFlow<List<NoteDomain>> =
        repository.getDeletedNotes()
            .map { list -> list.map { it.toDomain() } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val deletedTasks: StateFlow<List<TaskDomain>> =
        repository.getDeletedTasks()
            .map { list -> list.map { it.toDomain() } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Acciones para Tareas
    fun restoreTask(task: TaskDomain) {
        viewModelScope.launch { repository.restoreTask(task.id) }
    }

    fun reDeleteTask(id: Long, days: Long) {
        viewModelScope.launch { repository.reDeleteTask(id, days) }
    }

    fun deletePermanentTask(task: TaskDomain) {
        viewModelScope.launch { repository.permanentlyDeleteTask(task.id) }
    }

    // Acciones para Notas
    fun restoreNote(note: NoteDomain) {
        viewModelScope.launch { repository.restoreNote(note.id) }
    }

    fun reDeleteNote(id: Long, days: Long) {
        viewModelScope.launch { repository.reDeleteNote(id, days) }
    }

    fun deletePermanentNote(note: NoteDomain) {
        viewModelScope.launch { repository.permanentlyDeleteNote(note.id) }
    }
}
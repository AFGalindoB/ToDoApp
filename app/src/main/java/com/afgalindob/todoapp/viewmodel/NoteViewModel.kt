package com.afgalindob.todoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afgalindob.todoapp.data.local.entity.NoteEntity
import com.afgalindob.todoapp.data.repository.note.NoteRepository
import com.afgalindob.todoapp.data.mapper.NoteMapper.toDomain
import com.afgalindob.todoapp.domain.NoteDomain
import com.afgalindob.todoapp.domain.NoteFormState
import com.afgalindob.todoapp.domain.validation.validateNoteForm
import com.afgalindob.todoapp.utils.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    val notes: StateFlow<List<NoteEntity>> =
        repository.getNotes()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val notesDomain: StateFlow<List<NoteDomain>> =
        notes
            .map { list -> list.map { it.toDomain() } }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun validate(form: NoteFormState) = validateNoteForm(form)

    fun createNote(form: NoteFormState) {
        viewModelScope.launch {
            val now = DateUtils.now()
            val note = NoteEntity(
                title = form.title,
                content = form.content,
                createdAt = now,
                updatedAt = now
            )
            repository.insertNote(note)
        }
    }

    fun updateNote(note: NoteDomain, form: NoteFormState) {
        viewModelScope.launch {
            val updated = NoteEntity(
                id = note.id,
                title = form.title,
                content = form.content,
                createdAt = note.createdAt,
                updatedAt = DateUtils.now()
            )
            repository.updateNote(updated)
        }
    }

    fun deleteNote(note: NoteDomain) {
        viewModelScope.launch {
            repository.deleteNoteById(note.id)
        }
    }

}
package com.afgalindob.assistantapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afgalindob.assistantapp.data.local.entity.NoteEntity
import com.afgalindob.assistantapp.data.repository.note.NoteRepository
import com.afgalindob.assistantapp.data.mapper.NoteMapper.toDomain
import com.afgalindob.assistantapp.domain.NoteDomain
import com.afgalindob.assistantapp.domain.NoteFormState
import com.afgalindob.assistantapp.domain.validation.validateNoteForm
import com.afgalindob.assistantapp.utils.DateUtils
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
                updatedAt = now,
                deleteAt = 0L
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
                updatedAt = DateUtils.now(),
                deleteAt = 0L
            )
            repository.updateNote(updated)
        }
    }

    fun softDeleteNote(note: NoteDomain) {
        viewModelScope.launch {
            repository.setOnDeleteNote(id = note.id, days = 30)
        }
    }

    fun restoreNote(note: NoteDomain) {
        viewModelScope.launch {
            repository.restoreNote(note.id)
        }
    }

}
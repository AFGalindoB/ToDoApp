package com.afgalindob.todoapp.data.mapper

import com.afgalindob.todoapp.data.local.entity.NoteEntity
import com.afgalindob.todoapp.domain.NoteDomain
import com.afgalindob.todoapp.domain.NoteFormState

object NoteMapper{

    fun NoteDomain.toFormState(): NoteFormState {
        return NoteFormState(
            title = title,
            content = content
        )
    }

    fun NoteEntity.toDomain(): NoteDomain {
        return NoteDomain(
            id = id,
            title = title,
            content = content,
            createdAt = createdAt
        )
    }
}
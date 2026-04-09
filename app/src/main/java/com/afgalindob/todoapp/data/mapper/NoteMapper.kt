package com.afgalindob.todoapp.data.mapper

import com.afgalindob.todoapp.data.local.entity.NoteEntity
import com.afgalindob.todoapp.domain.NoteDomain

object NoteMapper{

    fun NoteEntity.toDomain(): NoteDomain {
        return NoteDomain(
            id = id,
            title = title,
            content = content,
            createdAt = createdAt
        )
    }
}
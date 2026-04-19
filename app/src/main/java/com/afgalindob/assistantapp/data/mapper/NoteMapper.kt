package com.afgalindob.assistantapp.data.mapper

import com.afgalindob.assistantapp.data.local.entity.NoteEntity
import com.afgalindob.assistantapp.domain.NoteDomain

object NoteMapper{

    fun NoteEntity.toDomain(): NoteDomain {
        return NoteDomain(
            id = id,
            title = title,
            content = content,
            createdAt = createdAt,
            deleteAt = if (deleteAt == 0L) null else deleteAt
        )
    }
}
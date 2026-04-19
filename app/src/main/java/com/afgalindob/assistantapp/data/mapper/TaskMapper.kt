package com.afgalindob.assistantapp.data.mapper

import com.afgalindob.assistantapp.data.local.entity.TaskEntity
import com.afgalindob.assistantapp.domain.TaskDomain

object TaskMapper {

    fun TaskEntity.toDomain(): TaskDomain {
        return TaskDomain(
            id = id,
            title = title,
            content = content,
            date = if (date == 0L) null else date,
            completed = completed,
            createdAt = createdAt,
            deleteAt = if (deleteAt == 0L) null else deleteAt
        )
    }

}
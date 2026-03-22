package com.afgalindob.todoapp.data.mapper

import com.afgalindob.todoapp.data.local.entity.TaskEntity
import com.afgalindob.todoapp.domain.TaskFormState
import com.afgalindob.todoapp.domain.TaskDomain

object TaskMapper {

    fun TaskDomain.toFormState(): TaskFormState {
        return TaskFormState(
            title = title,
            content = content,
            date = date,
            completed = completed
        )
    }

    fun TaskEntity.toDomain(): TaskDomain {
        return TaskDomain(
            id = id,
            title = title,
            content = content,
            date = if (date == 0L) null else date,
            completed = completed,
            createdAt = createdAt
        )
    }

}
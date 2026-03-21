package com.afgalindob.todoapp.schema

data class TaskFormState(
    val title: String = "",
    val content: String = "",
    val date: Long? = null,
    val completed: Boolean = false
)

data class TaskDomain(
    val id: Long,
    val title: String,
    val content: String,
    val date: Long?,
    val completed: Boolean,
    val createdAt: Long
)
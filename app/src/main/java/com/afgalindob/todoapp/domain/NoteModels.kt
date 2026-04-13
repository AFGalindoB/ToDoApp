package com.afgalindob.todoapp.domain

data class NoteFormState(
    val title: String = "",
    val content: String = ""
)

data class NoteDomain(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
    val deleteAt: Long?
)
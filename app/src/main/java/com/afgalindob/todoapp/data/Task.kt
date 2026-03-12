package com.afgalindob.todoapp.data

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val values: Map<String, String>
)

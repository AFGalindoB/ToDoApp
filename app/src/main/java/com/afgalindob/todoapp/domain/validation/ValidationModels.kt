package com.afgalindob.todoapp.domain.validation

data class FormField(
    val key: String,
    val required: Boolean = false,
    val maxLenghtChar: Int? = null
)

data class ValidationError(
    val resId: Int,
    val arg: Any? = null
)
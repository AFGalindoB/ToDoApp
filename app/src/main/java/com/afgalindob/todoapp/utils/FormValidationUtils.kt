package com.afgalindob.todoapp.utils

import com.afgalindob.todoapp.domain.TaskFormState

data class FormField(
    val key: String,
    val required: Boolean = false,
    val maxLenghtChar: Int? = null
)

object TaskValidationSchema {

    val fields = listOf(
        FormField(
            key = "title",
            required = true,
            maxLenghtChar = 70
        ),

        FormField(
            key = "content"
        ),

        FormField(
            key = "date"
        ),

        FormField(
            key = "completed"
        )
    )
}

// Validators genéricos
object Validators {
    fun required(value: Any?): Boolean {
        return when (value) {
            is String -> value.isNotBlank()
            else -> value != null
        }
    }

    fun maxLength(value: String?, max: Int): Boolean {
        return value?.length ?: 0 <= max
    }
}

fun validateTaskForm(form: TaskFormState): Map<String, String> {

    val errors = mutableMapOf<String, String>()

    TaskValidationSchema.fields.forEach { field ->
        val value = when (field.key) {
            "title" -> form.title
            "content" -> form.content
            "date" -> form.date
            "completed" -> form.completed
            else -> null
        }

        if (field.required && !Validators.required(value)) {
            errors[field.key] = "Este campo es obligatorio"
        }

        if (field.maxLenghtChar != null && value is String && !Validators.maxLength(value, field.maxLenghtChar)) {
            errors[field.key] = "Máximo ${field.maxLenghtChar} caracteres"
        }

    }

    return errors
}
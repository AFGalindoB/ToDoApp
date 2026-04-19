package com.afgalindob.assistantapp.domain.validation

import com.afgalindob.assistantapp.R
import com.afgalindob.assistantapp.domain.TaskFormState

object TaskValidationSchema {

    val fields = listOf(
        FormField(
            key = "title",
            required = true,
            maxLenghtChar = 70
        ),

        FormField(key = "content"),

        FormField(key = "date"),

        FormField(key = "completed")
    )
}
fun validateTaskForm(form: TaskFormState): Map<String, ValidationError> {

    val errors = mutableMapOf<String, ValidationError>()

    TaskValidationSchema.fields.forEach { field ->
        val value = when (field.key) {
            "title" -> form.title
            "content" -> form.content
            "date" -> form.date
            "completed" -> form.completed
            else -> null
        }

        if (field.required && !Validators.required(value)) {
            errors[field.key] = ValidationError(R.string.field_required)
        }

        if (field.maxLenghtChar != null && value is String &&
            !Validators.maxLength(value, field.maxLenghtChar)) {
                errors[field.key] = ValidationError(
                    resId = R.string.max_length,
                    arg = field.maxLenghtChar
                )
            }

    }

    return errors
}
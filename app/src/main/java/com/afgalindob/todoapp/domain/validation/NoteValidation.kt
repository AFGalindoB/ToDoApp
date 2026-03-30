package com.afgalindob.todoapp.domain.validation

import com.afgalindob.todoapp.R
import com.afgalindob.todoapp.domain.NoteFormState

object NoteValidationSchema {

    val fields = listOf(
        FormField(
            key = "title",
            required = true,
            maxLenghtChar = 70
        ),

        FormField(key = "content")
    )
}

fun validateNoteForm(form: NoteFormState): Map<String, ValidationError> {

    val errors = mutableMapOf<String, ValidationError>()

    NoteValidationSchema.fields.forEach { field ->
        val value = when (field.key) {
            "title" -> form.title
            "content" -> form.content
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
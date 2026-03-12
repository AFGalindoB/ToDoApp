package com.afgalindob.todoapp.schema

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType

/**
 * Representa el comportamiento de un tipo de campo dentro del sistema de formularios.
 * No describe qué campo existe, sino cómo se representa.
 *
 * Cada implementación define cómo se renderiza el campo tanto para:
 * - Entrada de datos (formularios)
 * - Visualización de datos (listas o detalles)
 *
 * Ejemplos de implementaciones:
 * TextFieldType, MultilineFieldType.
 */

sealed class FieldType {

    @Composable
    abstract fun RenderInput(
        field: FormField,
        values: MutableMap<String, String>
    )

    @Composable
    abstract fun RenderDisplay(
        field: FormField,
        values: Map<String, String>
    )
}

object TextFieldType : FieldType() {

    @Composable
    override fun RenderInput(
        field: FormField,
        values: MutableMap<String, String>
    ) {

        TextField(
            value = values[field.key] ?: "",
            onValueChange = {
                values[field.key] = it
            },
            label = { Text(stringResource(field.labelRes)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )

    }

    @Composable
    override fun RenderDisplay(
        field: FormField,
        values: Map<String, String>
    ) {

        Text(
            text = values[field.key] ?: ""
        )
    }
}

object MultilineFieldType : FieldType() {

    @Composable
    override fun RenderInput(
        field: FormField,
        values: MutableMap<String, String>
    ) {

        TextField(
            value = values[field.key] ?: "",
            onValueChange = {
                values[field.key] = it
            },
            label = { Text(stringResource(field.labelRes)) },
            singleLine = false,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )

    }

    @Composable
    override fun RenderDisplay(
        field: FormField,
        values: Map<String, String>
    ) {

        Text(
            text = values[field.key] ?: ""
        )
    }
}
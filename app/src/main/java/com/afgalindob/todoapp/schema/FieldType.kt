package com.afgalindob.todoapp.schema

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.clickable

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
        values: MutableMap<String, String>,
        onValueChange: (String,String) -> Unit
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
        values: MutableMap<String, String>,
        onValueChange: (String,String) -> Unit
    ) {

        TextField(
            value = values[field.key] ?: "",
            onValueChange = {
                onValueChange(field.key, it)
            },
            label = { Text(stringResource(field.labelRes)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words
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
        values: MutableMap<String, String>,
        onValueChange: (String,String) -> Unit
    ) {

        TextField(
            value = values[field.key] ?: "",
            onValueChange = {
                onValueChange(field.key, it)
            },
            label = { Text(stringResource(field.labelRes)) },
            singleLine = false,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
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

object BooleanFieldType : FieldType() {

    @Composable
    override fun RenderInput(
        field: FormField,
        values: MutableMap<String, String>,
        onValueChange: (String, String) -> Unit
    ) {

        val checked = values[field.key]?.toBoolean() ?: false

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = checked,
                onCheckedChange = {
                    onValueChange(field.key, it.toString())
                }
            )

            Text(stringResource(field.labelRes))
        }
    }

    @Composable
    override fun RenderDisplay(
        field: FormField,
        values: Map<String, String>
    ) {

        val checked = values[field.key]?.toBoolean() ?: false

        Text(
            text = if (checked) "✔ ${stringResource(field.labelRes)}"
            else "✘ ${stringResource(field.labelRes)}"
        )
    }
}

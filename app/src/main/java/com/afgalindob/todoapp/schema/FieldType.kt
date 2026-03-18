package com.afgalindob.todoapp.schema

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.afgalindob.todoapp.ui.dialogs.CalendarDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
        values: Map<String, String>,
        style: TextStyle = LocalTextStyle.current,
        colorText: Color
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
        values: Map<String, String>,
        style: TextStyle,
        colorText: Color
    ) {

        Text(
            text = values[field.key] ?: "",
            style = style,
            color = colorText
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
        values: Map<String, String>,
        style: TextStyle,
        colorText: Color
    ) {

        Text(
            text = values[field.key] ?: "",
            style = style,
            color = colorText
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
        values: Map<String, String>,
        style: TextStyle,
        colorText: Color
    ) {

        val checked = values[field.key]?.toBoolean() ?: false

        Text(
            text = if (checked) "✔ ${stringResource(field.labelRes)}"
            else "✘ ${stringResource(field.labelRes)}",
            style = style,
            color = colorText
        )
    }
}

object DateFieldType : FieldType() {

    private fun getDisplayDate(value: String?): String? {
        if (value.isNullOrBlank()) return null

        return try {
            // Intento 1: Formato corto que está causando el crash (yy-MM-dd)
            val inputFormatter = DateTimeFormatter.ofPattern("yy-MM-dd")
            val date = LocalDate.parse(value, inputFormatter)
            val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
            date.format(outputFormatter)
        } catch (e: Exception) {
            try {
                // Intento 2: Formato estándar ISO (yyyy-MM-dd)
                val date = LocalDate.parse(value)
                val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
                date.format(outputFormatter)
            } catch (e2: Exception) {
                // Si falla todo, devolvemos null para no romper la app
                null
            }
        }
    }

    @Composable
    override fun RenderInput(
        field: FormField,
        values: MutableMap<String, String>,
        onValueChange: (String, String) -> Unit
    ) {

        var showDialog by remember { mutableStateOf(false) }
        val value = values[field.key]

        val initialDate = value?.let {
            try {
                // Intentamos parsear con el formato corto o largo para el diálogo
                val inputFormatter = DateTimeFormatter.ofPattern("yy-MM-dd")
                LocalDate.parse(it, inputFormatter)
            } catch (e: Exception) {
                runCatching { LocalDate.parse(it) }.getOrElse { LocalDate.now() }
            }
        } ?: LocalDate.now()

        var selectedDate by remember(values[field.key]) {
            mutableStateOf(initialDate)
        }

        val displayText = getDisplayDate(value) ?: stringResource(field.labelRes)

        Text(
            text = displayText,

            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
                .padding(12.dp)
        )

        if (showDialog) {
            CalendarDialog(
                selectedDate = selectedDate,
                onDateSelected = { dateString ->
                    selectedDate = LocalDate.parse(dateString)
                    onValueChange(field.key, dateString)
                    showDialog = false
                },
                onDismiss = {
                    showDialog = false
                }
            )
        }
    }

    @Composable
    override fun RenderDisplay(
        field: FormField,
        values: Map<String, String>,
        style: TextStyle,
        colorText: Color
    ) {
        val value = values[field.key]

        if (value.isNullOrBlank()) return

        val displayDate = getDisplayDate(value)

        displayDate?.let {
            Text(text = it, style = style, color = colorText)
        }
    }
}
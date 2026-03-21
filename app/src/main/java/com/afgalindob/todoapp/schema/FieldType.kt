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
import com.afgalindob.todoapp.utils.DateUtils
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
            label = {
                field.labelRes?.let {
                    Text(stringResource(field.labelRes))
                }
            },
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
            label = {
                field.labelRes?.let {
                    Text(stringResource(field.labelRes))
                }
            },
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

            field.labelRes?.let {
                Text(stringResource(field.labelRes))
            }
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

        field.labelRes?.let {
            Text(
                text =
                    if (checked) "✔ ${stringResource(field.labelRes)}"
                    else "✘ ${stringResource(field.labelRes)}",
                style = style,
                color = colorText
            )
        }
    }
}

object DateFieldType : FieldType() {

    @Composable
    override fun RenderInput(
        field: FormField,
        values: MutableMap<String, String>,
        onValueChange: (String, String) -> Unit
    ) {
        var showDialog by remember { mutableStateOf(false) }

        // Obtener el valor actual como Long
        val valueLong = values[field.key]?.toLongOrNull() ?: 0L

        // Convertir Long a LocalDate
        val initialDate = if (valueLong == 0L) LocalDate.now()
        else DateUtils.fromTimestamp(valueLong)

        var selectedDate by remember(values[field.key]) {
            mutableStateOf(initialDate)
        }

        // Mostrar la fecha en un formato legible
        val displayText = if (valueLong == 0L) {
            field.labelRes?.let { stringResource(it) } ?: "Selecciona fecha"
        } else {
            DateUtils.formatReadable(selectedDate)
        }

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
                onDateSelected = { timestamp ->
                    val date = DateUtils.fromTimestamp(timestamp)
                    selectedDate = date
                    onValueChange(field.key, timestamp.toString())
                    showDialog = false
                },
                onDismiss = { showDialog = false }
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
        val valueLong = values[field.key]?.toLongOrNull() ?: return
        if (valueLong == 0L) return

        val date = DateUtils.fromTimestamp(valueLong)
        Text(text = DateUtils.formatReadable(date), style = style, color = colorText)
    }
}
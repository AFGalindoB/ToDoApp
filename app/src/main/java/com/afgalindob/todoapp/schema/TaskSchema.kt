package com.afgalindob.todoapp.schema

import com.afgalindob.todoapp.R

/**
 * Define el esquema de datos para las tareas de la aplicación.
 *
 * Este objeto actúa como la fuente de verdad sobre qué campos
 * componen una Task y cómo deben representarse en la interfaz.
 *
 * Las pantallas de creación, edición y visualización de tareas
 * utilizan este schema para generar automáticamente los campos.
 */

object TaskSchema {

    val fields = listOf(

        FormField(
            key = "title",
            labelRes = R.string.title_task,
            type = TextFieldType,
            required = true,
            maxLenghtChar = 70
        ),

        FormField(
            key = "description",
            labelRes = R.string.description,
            type = MultilineFieldType,
        ),

        FormField(
            key = "date",
            labelRes = R.string.date_input_label,
            type = DateFieldType
        ),

        FormField(
            key = "completed",
            labelRes = R.string.completed,
            type = BooleanFieldType,
        )

    )

}
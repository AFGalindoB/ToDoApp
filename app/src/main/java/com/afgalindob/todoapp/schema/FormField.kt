package com.afgalindob.todoapp.schema

/**
 * Describe un campo dentro de un formulario dinámico.
 *
 * No contiene los valores del usuario, solo la definición del campo.
 *
 * key: identificador único del campo usado para almacenar valores.
 * labelRes: recurso de texto mostrado en la interfaz.
 * type: comportamiento del campo (cómo se renderiza).
 */

data class FormField(
    val key: String,
    val labelRes: Int,
    val type: FieldType
)
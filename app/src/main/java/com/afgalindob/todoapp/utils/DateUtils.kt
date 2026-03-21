package com.afgalindob.todoapp.utils

import java.time.Instant
import java.time.ZoneId
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    fun toTimestamp(date: LocalDate): Long =
        date.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    fun now(): Long = System.currentTimeMillis()

    fun fromTimestamp(timestamp: Long): LocalDate =
        Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

    // 🔹 Formato técnico (para APIs, logs, etc.)
    fun formatISO(date: LocalDate): String =
        date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    // 🔹 Formato UI (el que tú quieres)
    fun formatReadable(date: LocalDate): String =
        date.format(
            DateTimeFormatter.ofPattern(
                "dd MMMM yyyy",
                Locale.getDefault()
            )
        )
}
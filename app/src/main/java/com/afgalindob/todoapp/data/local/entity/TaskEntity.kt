package com.afgalindob.todoapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    indices = [
        Index("date"),
        Index("completed")
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long  = 0,
    val title: String,
    val description: String,
    val date: String,
    val completed: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)

fun TaskEntity.toFormMap(): Map<String, String> =
    mapOf(
        "title" to title,
        "description" to description,
        "date" to date,
        "completed" to completed.toString()
    )
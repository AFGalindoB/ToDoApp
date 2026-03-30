package com.afgalindob.todoapp.data.container

import com.afgalindob.todoapp.data.repository.note.NoteRepository
import com.afgalindob.todoapp.data.repository.task.TaskRepository

interface AppContainer {
    val taskRepository: TaskRepository
    val noteRepository: NoteRepository
}
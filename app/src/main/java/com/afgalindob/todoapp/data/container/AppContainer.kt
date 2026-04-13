package com.afgalindob.todoapp.data.container

import com.afgalindob.todoapp.data.repository.note.NoteRepository
import com.afgalindob.todoapp.data.repository.task.TaskRepository
import com.afgalindob.todoapp.data.repository.trash.TrashRepository

interface AppContainer {
    val trashRepository: TrashRepository
    val taskRepository: TaskRepository
    val noteRepository: NoteRepository
}
package com.afgalindob.assistantapp.data.container

import com.afgalindob.assistantapp.data.repository.note.NoteRepository
import com.afgalindob.assistantapp.data.repository.task.TaskRepository
import com.afgalindob.assistantapp.data.repository.trash.TrashRepository

interface AppContainer {
    val trashRepository: TrashRepository
    val taskRepository: TaskRepository
    val noteRepository: NoteRepository
}
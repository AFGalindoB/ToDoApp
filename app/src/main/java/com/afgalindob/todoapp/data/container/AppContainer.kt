package com.afgalindob.todoapp.data.container

import com.afgalindob.todoapp.data.repository.TaskRepository

interface AppContainer {

    val taskRepository: TaskRepository

}
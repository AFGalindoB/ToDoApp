package com.afgalindob.todoapp

import android.app.Application
import com.afgalindob.todoapp.data.container.AppContainer
import com.afgalindob.todoapp.data.container.AppDataContainer

class TodoApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
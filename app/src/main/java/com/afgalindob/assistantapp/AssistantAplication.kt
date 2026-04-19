package com.afgalindob.assistantapp

import android.app.Application
import com.afgalindob.assistantapp.data.container.AppContainer
import com.afgalindob.assistantapp.data.container.AppDataContainer

class AssistantAplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
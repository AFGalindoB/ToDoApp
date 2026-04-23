package com.afgalindob.assistantapp.viewmodel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.afgalindob.assistantapp.data.container.AppContainer
import com.afgalindob.assistantapp.utils.LanguageUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class MainViewModel(private val container: AppContainer) : ViewModel() {
    private val _isAppReady = MutableStateFlow(false)
    val isAppReady = _isAppReady.asStateFlow()
    private val TAG = "MainViewModel_Startup"

    init {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val now = System.currentTimeMillis()

            Log.d(TAG, "Iniciando arranque en frío y limpieza de mantenimiento...")
            try {
                val taskCleanup = async { container.taskRepository.deleteExpiredTasks(now) }
                val noteCleanup = async { container.noteRepository.deleteExpiredNotes(now) }

                taskCleanup.await()
                noteCleanup.await()
                Log.i(TAG, "Mantenimiento completado con éxito.")

                container.taskRepository.getTasks(true, now).firstOrNull()
                Log.d(TAG, "Base de datos caliente y lista para peticiones.")

            } catch (e: Exception) {
                Log.e(TAG, "Error crítico durante el arranque en frío: ${e.message}", e)
            } finally {
                val endTime = System.currentTimeMillis()
                Log.i(TAG, "Arranque finalizado en ${endTime - startTime}ms. Liberando Splash Screen.")

                yield()
                delay(200)
                _isAppReady.value = true
            }
        }
    }
}
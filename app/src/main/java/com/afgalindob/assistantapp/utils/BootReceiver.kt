package com.afgalindob.assistantapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.afgalindob.assistantapp.AssistantAplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Accedemos al repositorio para recuperar la hora guardada
            val container = (context.applicationContext as AssistantAplication).container
            val userRepository = container.userRepository

            CoroutineScope(Dispatchers.IO).launch {
                val prefs = userRepository.userData.first() // Obtenemos el último valor guardado
                prefs?.reminderTime?.let { time ->
                    // Reprogramamos la alarma
                    AlarmScheduler.schedule(context, time)
                }
            }
        }
    }
}
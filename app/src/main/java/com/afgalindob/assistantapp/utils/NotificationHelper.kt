package com.afgalindob.assistantapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.afgalindob.assistantapp.R

object NotificationHelper {
    private const val CHANNEL_ID = "reminder_channel"

    fun showNotification(context: Context, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal para Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recordatorios Diarios",
                NotificationManager.IMPORTANCE_HIGH // CRITICO: Importancia alta
            ).apply {
                description = "Canal para las notificaciones del asistente"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.noir_assistant_monocromatic)
            .setContentTitle("Noir Assistant")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX) // Prioridad máxima
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)

        notificationManager.notify(1, builder.build())
    }
}
package com.afgalindob.assistantapp.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import java.util.Calendar

object AlarmScheduler {
    fun schedule(context: Context, time: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001, // ID fijo para evitar duplicados
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val parts = time.split(":")
        val hour = parts.getOrNull(0)?.toInt() ?: 8
        val minute = parts.getOrNull(1)?.toInt() ?: 0

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Si la hora ya pasó hoy, la programamos para mañana
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val isIgnoringBattery = powerManager.isIgnoringBatteryOptimizations(context.packageName)

        try {
            if (isIgnoringBattery) {
                // Solo usamos setAlarmClock si el usuario quitó las restricciones de batería
                val alarmInfo = AlarmManager.AlarmClockInfo(
                    calendar.timeInMillis,
                    pendingIntent
                )

                alarmManager.setAlarmClock(alarmInfo, pendingIntent)
                Log.d("AlarmScheduler", "Alarma de Reloj (Exacta) establecida para: ${calendar.time}")
            } else {
                // Fallback si no tiene optimización de batería desactivada
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Log.w("AlarmScheduler", "Batería optimizada. Usando fallback setAndAllowWhileIdle.")
            }
        } catch (e: SecurityException) {
            // Fallback en caso de que el permiso de alarmas exactas sea revocado o no concedido
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.w("AlarmScheduler", "Sin permiso de exacta. Usando fallback inexacto.")
        }
    }
}
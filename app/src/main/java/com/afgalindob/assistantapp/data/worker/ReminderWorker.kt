package com.afgalindob.assistantapp.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.afgalindob.assistantapp.AssistantAplication
import com.afgalindob.assistantapp.R
import com.afgalindob.assistantapp.data.local.entity.TaskEntity
import com.afgalindob.assistantapp.utils.DateUtils
import com.afgalindob.assistantapp.utils.NotificationHelper
import java.util.Calendar

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val container = (applicationContext as AssistantAplication).container
            val taskRepository = container.taskRepository

            // Obtenemos el timestamp de hoy (00:00:00)
            val todayStart = DateUtils.today()

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val endOfDay = calendar.timeInMillis

            // Obtenemos la lista de entidades
            val allPendingTasks = taskRepository.getPendingTasksForToday(endOfDay)

            if (allPendingTasks.isNotEmpty()) {
                val overdueTasks = allPendingTasks.filter { it.date < todayStart }
                val todayTasks = allPendingTasks.filter { it.date in todayStart..endOfDay }

                val message = buildNotificationMessage(overdueTasks.size, todayTasks.size, allPendingTasks)
                NotificationHelper.showNotification(applicationContext, message)
            } else {
                val context = applicationContext
                NotificationHelper.showNotification(
                    applicationContext,
                    context.getString(R.string.notification_no_tasks)
                )
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("ReminderWorker", "Error al procesar tareas en el Worker", e)
            Result.retry()
        }
    }

    private fun buildNotificationMessage(
        overdueCount: Int,
        todayCount: Int,
        allTasks: List<TaskEntity>
    ): String {

        val context = applicationContext

        return when {
            // Caso: Solo atrasadas
            overdueCount > 0 && todayCount == 0 -> {
                context.getString(R.string.notification_overdue_tasks, overdueCount)
            }
            // Caso: Solo para hoy
            overdueCount == 0 && todayCount > 0 -> {
                if (todayCount == 1) context.getString( R.string.notification_today_single, allTasks.first().title )
                else context.getString( R.string.notification_today_multiple, todayCount )
            }
            // Caso: Mixto
            overdueCount > 0 && todayCount > 0 -> {
                context.getString( R.string.notification_mixed_tasks, todayCount, overdueCount )
            }
            else -> context.getString(R.string.notification_generic_reminder)
        }
    }
}
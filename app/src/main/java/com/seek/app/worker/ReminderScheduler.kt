package com.seek.app.worker

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Schedules and cancels reminders using WorkManager.
 * Uses unique work names to prevent duplicate reminders.
 */
@Singleton
class ReminderScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    companion object {
        private const val WORK_NAME_PREFIX = "reminder:"
        const val KEY_REMINDER_ID = "reminderId"
    }
    
    /**
     * Schedule a reminder to fire at the specified time.
     * Uses REPLACE policy so rescheduling/snoozing replaces any existing work.
     */
    fun scheduleReminder(reminderId: String, triggerAtMillis: Long) {
        val delay = (triggerAtMillis - System.currentTimeMillis()).coerceAtLeast(0)
        
        val inputData = Data.Builder()
            .putString(KEY_REMINDER_ID, reminderId)
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(inputData)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        
        workManager.enqueueUniqueWork(
            uniqueWorkName = "$WORK_NAME_PREFIX$reminderId",
            existingWorkPolicy = ExistingWorkPolicy.REPLACE,
            request = workRequest
        )
    }
    
    /**
     * Cancel a scheduled reminder.
     */
    fun cancelReminder(reminderId: String) {
        workManager.cancelUniqueWork("$WORK_NAME_PREFIX$reminderId")
    }
    
    /**
     * Cancel all reminders for a specific application.
     * Note: This cancels by tag, requires adding tags to work requests.
     * For now, call cancelReminder for each reminder ID.
     */
    fun cancelAllRemindersForApplication(reminderIds: List<String>) {
        reminderIds.forEach { cancelReminder(it) }
    }
}

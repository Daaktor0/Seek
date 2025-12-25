package com.seek.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seek.app.data.database.ApplicationDao
import com.seek.app.data.database.MilestoneDao
import com.seek.app.data.database.ReminderDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker that fires reminder notifications.
 * Resilient: no-ops gracefully if reminder is missing, dismissed, or snoozed.
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val reminderDao: ReminderDao,
    private val milestoneDao: MilestoneDao,
    private val applicationDao: ApplicationDao,
    private val notificationHelper: NotificationHelper,
    private val reminderScheduler: ReminderScheduler
) : CoroutineWorker(appContext, params) {
    
    override suspend fun doWork(): Result {
        // 1. Get reminder ID from input data
        val reminderId = inputData.getString(ReminderScheduler.KEY_REMINDER_ID)
            ?: return Result.success() // No ID - nothing to do
        
        // 2. Load reminder from database
        val reminder = reminderDao.getReminderById(reminderId)
            ?: return Result.success() // Reminder deleted - nothing to do
        
        // 3. Check if dismissed
        if (reminder.isDismissed) {
            return Result.success() // Already dismissed - nothing to do
        }
        
        // 4. Check if snoozed (snooze time in future)
        val now = System.currentTimeMillis()
        if (reminder.snoozeUntil != null && reminder.snoozeUntil > now) {
            // Reschedule for snooze time
            reminderScheduler.scheduleReminder(reminder.id, reminder.snoozeUntil)
            return Result.success()
        }
        
        // 5. Check if notifications are enabled
        if (!notificationHelper.canShowNotifications()) {
            return Result.success() // Can't show notifications - fail gracefully
        }
        
        // 6. Load milestone and application details
        val milestone = milestoneDao.getMilestoneById(reminder.milestoneId)
        val application = applicationDao.getApplicationByIdOnce(reminder.applicationId)
        
        if (milestone == null || application == null) {
            return Result.success() // Data missing - nothing to do
        }
        
        // Check if application has notifications disabled
        if (!application.notificationsEnabled) {
            return Result.success() // User disabled notifications for this app
        }
        
        // 7. Show notification
        notificationHelper.showReminderNotification(
            reminderId = reminder.id,
            milestoneId = reminder.milestoneId,
            milestoneTitle = milestone.title,
            companyName = application.companyName,
            roleTitle = application.roleTitle
        )
        
        // 8. If this is the first reminder and there's no follow-up yet, schedule one
        if (!reminder.isFollowUp) {
            scheduleFollowUpIfNeeded(reminder)
        }
        
        return Result.success()
    }
    
    /**
     * Schedule a follow-up reminder if one doesn't exist.
     * Per PRD: one reminder + one optional follow-up nudge.
     */
    private suspend fun scheduleFollowUpIfNeeded(originalReminder: com.seek.app.data.model.ReminderEntity) {
        // Check if there's already an active follow-up
        val existingFollowUp = reminderDao.getActiveFollowUpForMilestone(originalReminder.milestoneId)
        if (existingFollowUp != null) {
            return // Already have a follow-up scheduled
        }
        
        // Schedule follow-up for 24 hours later
        val followUpTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000L)
        val followUpReminder = com.seek.app.data.model.ReminderEntity(
            id = java.util.UUID.randomUUID().toString(),
            milestoneId = originalReminder.milestoneId,
            applicationId = originalReminder.applicationId,
            scheduledTime = followUpTime,
            isFollowUp = true,
            isDismissed = false,
            isSnoozed = false,
            snoozeUntil = null,
            createdAt = System.currentTimeMillis()
        )
        
        reminderDao.insertReminder(followUpReminder)
        reminderScheduler.scheduleReminder(followUpReminder.id, followUpTime)
    }
}

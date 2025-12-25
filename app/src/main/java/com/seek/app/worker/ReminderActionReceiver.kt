package com.seek.app.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.seek.app.data.database.ReminderDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * BroadcastReceiver for handling reminder notification actions.
 * Handles Snooze and Dismiss actions.
 */
@AndroidEntryPoint
class ReminderActionReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var reminderDao: ReminderDao
    
    @Inject
    lateinit var reminderScheduler: ReminderScheduler
    
    private val scope = CoroutineScope(Dispatchers.IO)
    
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(NotificationHelper.EXTRA_REMINDER_ID) ?: return
        val milestoneId = intent.getStringExtra(NotificationHelper.EXTRA_MILESTONE_ID) ?: ""
        
        // Cancel the notification immediately
        NotificationManagerCompat.from(context).cancel(reminderId.hashCode())
        
        when (intent.action) {
            NotificationHelper.ACTION_SNOOZE_1H -> {
                handleSnooze(reminderId)
            }
            NotificationHelper.ACTION_DISMISS -> {
                handleDismiss(reminderId, milestoneId)
            }
        }
    }
    
    private fun handleSnooze(reminderId: String) {
        scope.launch {
            val newTime = System.currentTimeMillis() + (60 * 60 * 1000L) // 1 hour
            
            // Update database
            reminderDao.snoozeReminder(reminderId, newTime)
            
            // Reschedule work
            reminderScheduler.scheduleReminder(reminderId, newTime)
        }
    }
    
    private fun handleDismiss(reminderId: String, milestoneId: String) {
        scope.launch {
            // Get reminder to check if it's the primary (not a follow-up)
            val reminder = reminderDao.getReminderById(reminderId)
            
            // Dismiss this reminder
            reminderDao.dismissReminder(reminderId)
            reminderScheduler.cancelReminder(reminderId)
            
            // PRD rule: If dismissing primary reminder, also dismiss its follow-up
            if (reminder != null && !reminder.isFollowUp && milestoneId.isNotEmpty()) {
                val followUp = reminderDao.getActiveFollowUpForMilestone(milestoneId)
                if (followUp != null) {
                    reminderDao.dismissReminder(followUp.id)
                    reminderScheduler.cancelReminder(followUp.id)
                }
            }
        }
    }
}

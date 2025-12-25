package com.seek.app.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.seek.app.MainActivity
import com.seek.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper for showing reminder notifications.
 * Handles notification channel creation and permission checks.
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "seek_reminders"
        const val CHANNEL_NAME = "Reminders"
        const val CHANNEL_DESCRIPTION = "Gentle reminders for your job applications"
        
        // Action intents
        const val ACTION_SNOOZE_1H = "com.seek.app.action.REMINDER_SNOOZE_1H"
        const val ACTION_DISMISS = "com.seek.app.action.REMINDER_DISMISS"
        const val EXTRA_REMINDER_ID = "reminderId"
        const val EXTRA_MILESTONE_ID = "milestoneId"
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // Calm, non-intrusive
            ).apply {
                description = CHANNEL_DESCRIPTION
                // Calm notification - gentle vibration
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200)
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
    
    /**
     * Check if we can show notifications.
     */
    fun canShowNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }
    
    /**
     * Show a reminder notification with Snooze and Dismiss actions.
     */
    fun showReminderNotification(
        reminderId: String,
        milestoneId: String,
        milestoneTitle: String,
        companyName: String,
        roleTitle: String
    ) {
        if (!canShowNotifications()) return
        
        val notificationId = reminderId.hashCode()
        
        // Content intent - open app
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // TODO: Add deep link to specific application
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Snooze action - snooze for 1 hour
        val snoozeIntent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = ACTION_SNOOZE_1H
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_MILESTONE_ID, milestoneId)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 1, // Unique request code
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Dismiss action
        val dismissIntent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = ACTION_DISMISS
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_MILESTONE_ID, milestoneId)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 2, // Unique request code
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO: Use proper icon
            .setContentTitle("Next step: $milestoneTitle")
            .setContentText("For $companyName — $roleTitle")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("For $companyName — $roleTitle"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(0, "Snooze 1h", snoozePendingIntent)
            .addAction(0, "Dismiss", dismissPendingIntent)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Permission denied - silently fail
        }
    }
    
    /**
     * Overload for backward compatibility (without milestoneId).
     */
    fun showReminderNotification(
        reminderId: String,
        milestoneTitle: String,
        companyName: String,
        roleTitle: String
    ) {
        showReminderNotification(reminderId, "", milestoneTitle, companyName, roleTitle)
    }
    
    /**
     * Cancel a specific notification.
     */
    fun cancelNotification(reminderId: String) {
        NotificationManagerCompat.from(context).cancel(reminderId.hashCode())
    }
}

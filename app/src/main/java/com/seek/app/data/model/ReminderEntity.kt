package com.seek.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for reminders.
 * Used by WorkManager to schedule notifications.
 */
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = MilestoneEntity::class,
            parentColumns = ["id"],
            childColumns = ["milestoneId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["milestoneId"])]
)
data class ReminderEntity(
    @PrimaryKey
    val id: String,
    val milestoneId: String,
    val applicationId: String, // Denormalized for efficient queries
    val scheduledTime: Long,
    val isFollowUp: Boolean, // true = second nudge, false = first reminder
    val isDismissed: Boolean,
    val isSnoozed: Boolean,
    val snoozeUntil: Long?, // When snooze expires
    val createdAt: Long
)

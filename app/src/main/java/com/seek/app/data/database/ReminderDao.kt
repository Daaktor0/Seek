package com.seek.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seek.app.data.model.ReminderEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for reminders.
 */
@Dao
interface ReminderDao {
    
    @Query("SELECT * FROM reminders WHERE applicationId = :applicationId")
    fun getRemindersForApplication(applicationId: String): Flow<List<ReminderEntity>>
    
    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: String): ReminderEntity?
    
    @Query("SELECT * FROM reminders WHERE scheduledTime <= :time AND isDismissed = 0 AND (snoozeUntil IS NULL OR snoozeUntil <= :time)")
    suspend fun getPendingReminders(time: Long = System.currentTimeMillis()): List<ReminderEntity>
    
    @Query("SELECT * FROM reminders WHERE isDismissed = 0 ORDER BY scheduledTime ASC")
    fun getActiveReminders(): Flow<List<ReminderEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderEntity>)
    
    @Update
    suspend fun updateReminder(reminder: ReminderEntity)
    
    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)
    
    @Query("DELETE FROM reminders WHERE applicationId = :applicationId")
    suspend fun deleteRemindersForApplication(applicationId: String)
    
    @Query("DELETE FROM reminders")
    suspend fun deleteAllReminders()
    
    @Query("UPDATE reminders SET isDismissed = 1 WHERE id = :id")
    suspend fun dismissReminder(id: String)
    
    @Query("UPDATE reminders SET isSnoozed = 1, snoozeUntil = :snoozeUntil WHERE id = :id")
    suspend fun snoozeReminder(id: String, snoozeUntil: Long)
    
    @Query("SELECT * FROM reminders WHERE milestoneId = :milestoneId")
    suspend fun getRemindersForMilestone(milestoneId: String): List<ReminderEntity>
    
    @Query("""
        SELECT * FROM reminders 
        WHERE milestoneId = :milestoneId AND isFollowUp = 1 AND isDismissed = 0 
        ORDER BY scheduledTime DESC 
        LIMIT 1
    """)
    suspend fun getActiveFollowUpForMilestone(milestoneId: String): ReminderEntity?
    
    @Query("DELETE FROM reminders WHERE milestoneId = :milestoneId")
    suspend fun deleteRemindersForMilestone(milestoneId: String)
}

package com.seek.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seek.app.data.model.ApplicationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for applications.
 */
@Dao
interface ApplicationDao {
    
    @Query("SELECT * FROM applications ORDER BY updatedAt DESC")
    fun getAllApplications(): Flow<List<ApplicationEntity>>
    
    @Query("SELECT * FROM applications WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getActiveApplications(): Flow<List<ApplicationEntity>>
    
    @Query("SELECT * FROM applications WHERE isArchived = 1 ORDER BY updatedAt DESC")
    fun getArchivedApplications(): Flow<List<ApplicationEntity>>
    
    @Query("SELECT * FROM applications WHERE id = :id")
    fun getApplicationById(id: String): Flow<ApplicationEntity?>
    
    @Query("SELECT * FROM applications WHERE id = :id")
    suspend fun getApplicationByIdOnce(id: String): ApplicationEntity?
    
    @Query("SELECT COUNT(*) FROM applications WHERE isArchived = 0")
    fun getActiveApplicationCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM applications WHERE isArchived = 0")
    suspend fun getActiveApplicationCountOnce(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: ApplicationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplications(applications: List<ApplicationEntity>)
    
    @Update
    suspend fun updateApplication(application: ApplicationEntity)
    
    @Delete
    suspend fun deleteApplication(application: ApplicationEntity)
    
    @Query("DELETE FROM applications WHERE id = :id")
    suspend fun deleteApplicationById(id: String)
    
    @Query("DELETE FROM applications")
    suspend fun deleteAllApplications()
    
    @Query("UPDATE applications SET isArchived = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun archiveApplication(id: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE applications SET isArchived = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun unarchiveApplication(id: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE applications SET status = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE applications SET notificationsEnabled = :enabled, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateNotificationsEnabled(id: String, enabled: Boolean, timestamp: Long = System.currentTimeMillis())
}

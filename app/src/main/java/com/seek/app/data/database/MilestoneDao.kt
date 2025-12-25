package com.seek.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seek.app.data.model.MilestoneEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for milestones.
 */
@Dao
interface MilestoneDao {
    
    @Query("SELECT * FROM milestones WHERE applicationId = :applicationId ORDER BY `order` ASC")
    fun getMilestonesForApplication(applicationId: String): Flow<List<MilestoneEntity>>
    
    @Query("SELECT * FROM milestones WHERE applicationId = :applicationId ORDER BY `order` ASC")
    suspend fun getMilestonesForApplicationOnce(applicationId: String): List<MilestoneEntity>
    
    @Query("SELECT * FROM milestones WHERE id = :id")
    suspend fun getMilestoneById(id: String): MilestoneEntity?
    
    @Query("SELECT * FROM milestones WHERE applicationId = :applicationId AND isPrimary = 1 AND isCompleted = 0 LIMIT 1")
    suspend fun getPrimaryMilestone(applicationId: String): MilestoneEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: MilestoneEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<MilestoneEntity>)
    
    @Update
    suspend fun updateMilestone(milestone: MilestoneEntity)
    
    @Delete
    suspend fun deleteMilestone(milestone: MilestoneEntity)
    
    @Query("DELETE FROM milestones WHERE applicationId = :applicationId")
    suspend fun deleteMilestonesForApplication(applicationId: String)
    
    @Query("DELETE FROM milestones")
    suspend fun deleteAllMilestones()
    
    @Query("UPDATE milestones SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: String, completed: Boolean)
    
    /**
     * Set a milestone as primary and unset all others for this application.
     */
    @Query("UPDATE milestones SET isPrimary = 0 WHERE applicationId = :applicationId")
    suspend fun clearPrimaryForApplication(applicationId: String)
    
    @Query("UPDATE milestones SET isPrimary = 1 WHERE id = :id")
    suspend fun setPrimary(id: String)
}

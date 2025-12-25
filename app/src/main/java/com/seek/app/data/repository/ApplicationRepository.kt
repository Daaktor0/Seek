package com.seek.app.data.repository

import com.seek.app.domain.model.Application
import com.seek.app.domain.model.Milestone
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for job applications.
 * Abstracts data source from domain layer.
 */
interface ApplicationRepository {
    
    /**
     * Get all applications as a Flow.
     */
    fun getAllApplications(): Flow<List<Application>>
    
    /**
     * Get only active (non-archived) applications.
     */
    fun getActiveApplications(): Flow<List<Application>>
    
    /**
     * Get only archived applications.
     */
    fun getArchivedApplications(): Flow<List<Application>>
    
    /**
     * Get a single application by ID.
     */
    fun getApplicationById(id: String): Flow<Application?>
    
    /**
     * Get count of active applications (for entitlement checking).
     */
    fun getActiveApplicationCount(): Flow<Int>
    
    /**
     * Add a new application with default milestones.
     */
    suspend fun addApplication(application: Application): String
    
    /**
     * Update an existing application.
     */
    suspend fun updateApplication(application: Application)
    
    /**
     * Delete an application and its milestones.
     */
    suspend fun deleteApplication(id: String)
    
    /**
     * Archive an application (removes from active count).
     */
    suspend fun archiveApplication(id: String)
    
    /**
     * Unarchive an application.
     */
    suspend fun unarchiveApplication(id: String)
    
    /**
     * Update notification setting for an application.
     */
    suspend fun setNotificationsEnabled(id: String, enabled: Boolean)
    
    /**
     * Get milestones for an application.
     */
    fun getMilestonesForApplication(applicationId: String): Flow<List<Milestone>>
    
    /**
     * Add a milestone.
     */
    suspend fun addMilestone(milestone: Milestone)
    
    /**
     * Update a milestone.
     */
    suspend fun updateMilestone(milestone: Milestone)
    
    /**
     * Set a milestone as primary (and clear others).
     */
    suspend fun setPrimaryMilestone(applicationId: String, milestoneId: String)
    
    /**
     * Mark milestone as completed.
     */
    suspend fun completeMilestone(milestoneId: String)
    
    /**
     * Delete all data (for wipe feature).
     */
    suspend fun wipeAllData()
    
    /**
     * Export all data as JSON.
     */
    suspend fun exportToJson(): String
    
    /**
     * Export all data as CSV.
     */
    suspend fun exportToCsv(): String
}

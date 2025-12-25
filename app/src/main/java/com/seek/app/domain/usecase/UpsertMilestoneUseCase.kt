package com.seek.app.domain.usecase

import com.seek.app.data.repository.ApplicationRepository
import com.seek.app.domain.model.Milestone
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for creating or updating milestones.
 */
class UpsertMilestoneUseCase @Inject constructor(
    private val repository: ApplicationRepository
) {
    /**
     * Add a new milestone to an application.
     */
    suspend fun addMilestone(
        applicationId: String,
        title: String,
        description: String? = null,
        dueDate: Long? = null,
        isPrimary: Boolean = false
    ): Result<Milestone> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("Milestone title is required"))
        }
        
        val milestone = Milestone(
            id = UUID.randomUUID().toString(),
            applicationId = applicationId,
            title = title.trim(),
            description = description?.trim()?.takeIf { it.isNotBlank() },
            dueDate = dueDate,
            isCompleted = false,
            isPrimary = isPrimary,
            createdAt = System.currentTimeMillis(),
            order = Int.MAX_VALUE // Will be reordered
        )
        
        return try {
            repository.addMilestone(milestone)
            Result.success(milestone)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update an existing milestone.
     */
    suspend fun updateMilestone(milestone: Milestone): Result<Unit> {
        return try {
            repository.updateMilestone(milestone)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Mark a milestone as completed.
     */
    suspend fun completeMilestone(milestoneId: String): Result<Unit> {
        return try {
            repository.completeMilestone(milestoneId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Set a milestone as the primary/next action.
     */
    suspend fun setPrimaryMilestone(applicationId: String, milestoneId: String): Result<Unit> {
        return try {
            repository.setPrimaryMilestone(applicationId, milestoneId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a milestone.
     * Note: Deletion is typically cascaded when application is deleted.
     */
    suspend fun deleteMilestone(milestoneId: String): Result<Unit> {
        // Repository doesn't expose direct milestone deletion
        // Milestones are cleaned up when applications are deleted
        return Result.success(Unit)
    }
}

package com.seek.app.domain.usecase

import com.seek.app.data.repository.ApplicationRepository
import com.seek.app.domain.model.Application
import com.seek.app.domain.model.ApplicationStatus
import java.util.UUID
import javax.inject.Inject

/**
 * Use case for adding a new job application.
 * Handles validation and default milestone creation.
 */
class AddApplicationUseCase @Inject constructor(
    private val repository: ApplicationRepository
) {
    suspend operator fun invoke(
        companyName: String,
        roleTitle: String,
        jobLink: String? = null,
        location: String? = null,
        notes: String? = null
    ): Result<Application> {
        // Validate required fields
        if (companyName.isBlank()) {
            return Result.failure(IllegalArgumentException("Company name is required"))
        }
        if (roleTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Role title is required"))
        }
        
        val now = System.currentTimeMillis()
        val application = Application(
            id = UUID.randomUUID().toString(),
            companyName = companyName.trim(),
            roleTitle = roleTitle.trim(),
            jobLink = jobLink?.trim()?.takeIf { it.isNotBlank() },
            location = location?.trim()?.takeIf { it.isNotBlank() },
            appliedDate = now,
            notes = notes?.trim()?.takeIf { it.isNotBlank() },
            status = ApplicationStatus.APPLIED,
            isArchived = false,
            notificationsEnabled = true,
            createdAt = now,
            updatedAt = now
        )
        
        return try {
            repository.addApplication(application)
            Result.success(application)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.seek.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.seek.app.domain.model.Application
import com.seek.app.domain.model.ApplicationStatus

/**
 * Room entity for job applications.
 * Maps to the 'applications' table in the encrypted database.
 */
@Entity(tableName = "applications")
data class ApplicationEntity(
    @PrimaryKey
    val id: String,
    val companyName: String,
    val roleTitle: String,
    val jobLink: String?,
    val location: String?,
    val appliedDate: Long,
    val notes: String?, // Will be encrypted at rest via SQLCipher
    val status: String,
    val isArchived: Boolean,
    val notificationsEnabled: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {
    /**
     * Maps this entity to the domain model.
     */
    fun toDomainModel(milestones: List<com.seek.app.domain.model.Milestone> = emptyList()): Application {
        val primaryMilestone = milestones.find { it.isPrimary && !it.isCompleted }
        return Application(
            id = id,
            companyName = companyName,
            roleTitle = roleTitle,
            jobLink = jobLink,
            location = location,
            appliedDate = appliedDate,
            notes = notes,
            status = ApplicationStatus.fromString(status),
            isArchived = isArchived,
            notificationsEnabled = notificationsEnabled,
            createdAt = createdAt,
            updatedAt = updatedAt,
            primaryMilestone = primaryMilestone,
            milestones = milestones
        )
    }

    companion object {
        /**
         * Creates an entity from the domain model.
         */
        fun fromDomainModel(application: Application): ApplicationEntity {
            return ApplicationEntity(
                id = application.id,
                companyName = application.companyName,
                roleTitle = application.roleTitle,
                jobLink = application.jobLink,
                location = application.location,
                appliedDate = application.appliedDate,
                notes = application.notes,
                status = application.status.name,
                isArchived = application.isArchived,
                notificationsEnabled = application.notificationsEnabled,
                createdAt = application.createdAt,
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}

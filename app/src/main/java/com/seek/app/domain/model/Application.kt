package com.seek.app.domain.model

import java.util.UUID

/**
 * Domain model for a job application.
 * This represents the clean, business-logic focused version of the data.
 */
data class Application(
    val id: String = UUID.randomUUID().toString(),
    val companyName: String,
    val roleTitle: String,
    val jobLink: String? = null,
    val location: String? = null,
    val appliedDate: Long = System.currentTimeMillis(),
    val notes: String? = null,
    val status: ApplicationStatus = ApplicationStatus.APPLIED,
    val isArchived: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    // The current primary milestone for "next action" display
    val primaryMilestone: Milestone? = null,
    // All milestones for this application
    val milestones: List<Milestone> = emptyList()
) {
    /**
     * Returns the next action text to display.
     * This is the core "calm guidance" feature.
     */
    fun getNextActionText(): String {
        return primaryMilestone?.title ?: when (status) {
            ApplicationStatus.APPLIED -> "Wait for response (typical: 1-2 weeks)"
            ApplicationStatus.INTERVIEWING -> "Prepare for interview"
            ApplicationStatus.OFFERED -> "Review offer details"
            ApplicationStatus.ACCEPTED -> "Celebrate! 🎉"
            ApplicationStatus.DECLINED -> "Application closed"
            ApplicationStatus.NO_RESPONSE -> "Consider following up"
            ApplicationStatus.ARCHIVED -> "Archived"
        }
    }
    
    /**
     * Returns whether this application counts toward active slot limits.
     */
    fun countsTowardLimit(): Boolean = !isArchived
}

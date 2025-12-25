package com.seek.app.domain.model

import java.util.UUID

/**
 * Domain model for a milestone/next step within an application.
 * Milestones help users track progress and know what to do next.
 */
data class Milestone(
    val id: String = UUID.randomUUID().toString(),
    val applicationId: String,
    val title: String,
    val description: String? = null,
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
    val isPrimary: Boolean = false, // Only one milestone per application should be primary
    val createdAt: Long = System.currentTimeMillis(),
    val order: Int = 0 // For ordering milestones
) {
    companion object {
        /**
         * Default milestones suggested when a new application is created.
         * These are meant to be calm, helpful suggestions - not pressure.
         */
        fun defaultMilestonesFor(applicationId: String): List<Milestone> {
            val now = System.currentTimeMillis()
            val oneWeek = 7 * 24 * 60 * 60 * 1000L
            val twoWeeks = 14 * 24 * 60 * 60 * 1000L
            
            return listOf(
                Milestone(
                    applicationId = applicationId,
                    title = "Wait for initial response",
                    description = "Companies typically respond within 1-2 weeks",
                    dueDate = now + oneWeek,
                    isPrimary = true,
                    order = 0
                ),
                Milestone(
                    applicationId = applicationId,
                    title = "Consider follow-up",
                    description = "If no response, a polite follow-up email can help",
                    dueDate = now + twoWeeks,
                    order = 1
                )
            )
        }
    }
}

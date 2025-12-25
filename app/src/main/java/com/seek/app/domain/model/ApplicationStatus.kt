package com.seek.app.domain.model

/**
 * Status of a job application.
 * Note: No "Rejected" status has red/alarm colors - we use neutral styling.
 */
enum class ApplicationStatus(val displayName: String, val description: String) {
    APPLIED("Applied", "Waiting to hear back"),
    INTERVIEWING("Interviewing", "In the interview process"),
    OFFERED("Offered", "Received an offer!"),
    ACCEPTED("Accepted", "You got the job!"),
    DECLINED("Declined", "You decided not to proceed"),
    NO_RESPONSE("No Response", "Haven't heard back yet"),
    ARCHIVED("Archived", "No longer active");
    
    companion object {
        fun fromString(value: String): ApplicationStatus {
            return entries.find { it.name == value } ?: APPLIED
        }
    }
}

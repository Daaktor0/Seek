package com.seek.app.domain.usecase

import com.seek.app.data.repository.ApplicationRepository
import javax.inject.Inject

/**
 * Use case for wiping all application data.
 * Used during offboarding or privacy reset.
 */
class WipeDataUseCase @Inject constructor(
    private val repository: ApplicationRepository
) {
    /**
     * Delete all applications, milestones, and reminders.
     * This is irreversible.
     */
    suspend operator fun invoke(): Result<Unit> {
        return try {
            repository.wipeAllData()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.seek.app.domain.usecase

import com.seek.app.data.repository.ApplicationRepository
import com.seek.app.domain.model.Application
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving applications.
 * Provides active, archived, and single application queries.
 */
class GetApplicationsUseCase @Inject constructor(
    private val repository: ApplicationRepository
) {
    /**
     * Get all active (non-archived) applications as a Flow.
     */
    fun getActiveApplications(): Flow<List<Application>> {
        return repository.getActiveApplications()
    }
    
    /**
     * Get all archived applications as a Flow.
     */
    fun getArchivedApplications(): Flow<List<Application>> {
        return repository.getArchivedApplications()
    }
    
    /**
     * Get a single application by ID as a Flow.
     */
    fun getApplicationById(id: String): Flow<Application?> {
        return repository.getApplicationById(id)
    }
    
    /**
     * Get the count of active applications.
     */
    fun getActiveCount(): Flow<Int> {
        return repository.getActiveApplicationCount()
    }
}

package com.seek.app.domain.usecase

import com.seek.app.data.repository.ApplicationRepository
import com.seek.app.domain.model.Milestone
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving milestones for an application.
 */
class GetMilestonesForApplicationUseCase @Inject constructor(
    private val repository: ApplicationRepository
) {
    /**
     * Get all milestones for an application as a Flow.
     */
    operator fun invoke(applicationId: String): Flow<List<Milestone>> {
        return repository.getMilestonesForApplication(applicationId)
    }
}

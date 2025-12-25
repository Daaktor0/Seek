package com.seek.app.domain.usecase

import com.seek.app.data.entitlement.EntitlementProvider
import com.seek.app.data.repository.ApplicationRepository
import com.seek.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case for checking entitlements and slot availability.
 */
class CheckEntitlementUseCase @Inject constructor(
    private val entitlementProvider: EntitlementProvider,
    private val repository: ApplicationRepository
) {
    /**
     * Get user settings as a Flow.
     */
    fun getUserSettings(): Flow<UserSettings> {
        return entitlementProvider.getUserSettings()
    }
    
    /**
     * Check if user can add a new application.
     */
    fun canAddApplication(): Flow<Boolean> {
        return combine(
            repository.getActiveApplicationCount(),
            entitlementProvider.getUserSettings()
        ) { activeCount, settings ->
            activeCount < settings.getMaxActiveSlots()
        }
    }
    
    /**
     * Get remaining slots available.
     */
    fun getRemainingSlots(): Flow<Int> {
        return combine(
            repository.getActiveApplicationCount(),
            entitlementProvider.getUserSettings()
        ) { activeCount, settings ->
            (settings.getMaxActiveSlots() - activeCount).coerceAtLeast(0)
        }
    }
    
    /**
     * Check if user has an active subscription.
     */
    fun isSubscribed(): Flow<Boolean> {
        return combine(
            entitlementProvider.getUserSettings()
        ) { settings ->
            settings.first().subscriptionActive
        }
    }
    
    /**
     * Purchase subscription (delegates to provider).
     */
    suspend fun purchaseSubscription(): Result<Unit> {
        return try {
            entitlementProvider.purchaseSubscription()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Purchase additional slot pack (delegates to provider).
     */
    suspend fun purchaseSlotPack(): Result<Unit> {
        return try {
            entitlementProvider.purchaseSlotPack()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

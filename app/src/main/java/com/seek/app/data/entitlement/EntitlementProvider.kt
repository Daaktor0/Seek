package com.seek.app.data.entitlement

import com.seek.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for entitlement/subscription checking.
 * This is abstracted so we can swap in real Play Billing later.
 */
interface EntitlementProvider {
    fun getUserSettings(): Flow<UserSettings>
    suspend fun updateSettings(settings: UserSettings)
    suspend fun purchaseSubscription(): Boolean
    suspend fun purchaseSlotPack(): Boolean
    suspend fun restorePurchases(): Boolean
}

/**
 * Fake implementation for MVP.
 * Stores entitlements in local state only.
 */
@Singleton
class FakeEntitlementProvider @Inject constructor() : EntitlementProvider {
    
    private val _settings = MutableStateFlow(UserSettings())
    
    override fun getUserSettings(): Flow<UserSettings> = _settings.asStateFlow()
    
    override suspend fun updateSettings(settings: UserSettings) {
        _settings.value = settings
    }
    
    override suspend fun purchaseSubscription(): Boolean {
        // Simulate successful purchase
        _settings.value = _settings.value.copy(subscriptionActive = true)
        return true
    }
    
    override suspend fun purchaseSlotPack(): Boolean {
        // Simulate successful purchase (+5 slots)
        _settings.value = _settings.value.copy(
            additionalSlotsPurchased = _settings.value.additionalSlotsPurchased + 1
        )
        return true
    }
    
    override suspend fun restorePurchases(): Boolean {
        // In fake implementation, nothing to restore
        return true
    }
}

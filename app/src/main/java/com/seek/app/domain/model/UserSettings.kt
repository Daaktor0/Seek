package com.seek.app.domain.model

/**
 * User settings and preferences.
 * All settings are designed with privacy-first approach.
 */
data class UserSettings(
    val researchAssistanceEnabled: Boolean = true, // Public info only
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean? = null, // null = follow system
    val hasSeenOnboarding: Boolean = false,
    val hasOptedIntoAnalytics: Boolean = false, // Must be explicit opt-in
    // Entitlement state
    val subscriptionActive: Boolean = false,
    val additionalSlotsPurchased: Int = 0,
    // BYO Supabase sync (optional)
    val supabaseUrl: String? = null,
    val supabaseAnonKey: String? = null, // User's own key
    val syncEnabled: Boolean = false
) {
    /**
     * Calculate total allowed active application slots.
     * Free: 3, Subscription: 18 (3+15), One-time: +5 each
     */
    fun getMaxActiveSlots(): Int {
        val base = 3
        val subscriptionBonus = if (subscriptionActive) 15 else 0
        return base + subscriptionBonus + (additionalSlotsPurchased * 5)
    }
    
    /**
     * Check if user can add more active applications.
     */
    fun canAddApplication(currentActiveCount: Int): Boolean {
        return currentActiveCount < getMaxActiveSlots()
    }
    
    /**
     * Get remaining available slots.
     */
    fun getRemainingSlots(currentActiveCount: Int): Int {
        return (getMaxActiveSlots() - currentActiveCount).coerceAtLeast(0)
    }
}

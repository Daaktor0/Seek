package com.seek.app.ui.screen.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seek.app.data.entitlement.EntitlementProvider
import com.seek.app.data.repository.ApplicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaywallUiState(
    val activeCount: Int = 0,
    val maxSlots: Int = 3,
    val isSubscribed: Boolean = false,
    val additionalPacks: Int = 0,
    val isLoading: Boolean = false,
    val purchaseSuccess: Boolean = false
)

@HiltViewModel
class PaywallViewModel @Inject constructor(
    private val repository: ApplicationRepository,
    private val entitlementProvider: EntitlementProvider
) : ViewModel() {
    
    val uiState: StateFlow<PaywallUiState> = combine(
        repository.getActiveApplicationCount(),
        entitlementProvider.getUserSettings()
    ) { count, settings ->
        PaywallUiState(
            activeCount = count,
            maxSlots = settings.getMaxActiveSlots(),
            isSubscribed = settings.subscriptionActive,
            additionalPacks = settings.additionalSlotsPurchased
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PaywallUiState()
    )
    
    private val _purchaseSuccess = MutableStateFlow(false)
    
    fun purchaseSubscription() {
        viewModelScope.launch {
            // Using FakeEntitlementProvider for now
            entitlementProvider.purchaseSubscription()
            _purchaseSuccess.value = true
        }
    }
    
    fun purchaseSlotPack() {
        viewModelScope.launch {
            // Using FakeEntitlementProvider for now
            entitlementProvider.purchaseSlotPack()
            _purchaseSuccess.value = true
        }
    }
}

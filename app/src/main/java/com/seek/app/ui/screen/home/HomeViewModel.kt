package com.seek.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seek.app.data.entitlement.EntitlementProvider
import com.seek.app.data.repository.ApplicationRepository
import com.seek.app.domain.model.Application
import com.seek.app.domain.model.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val applications: List<Application> = emptyList(),
    val isLoading: Boolean = true,
    val activeCount: Int = 0,
    val settings: UserSettings = UserSettings(),
    val selectedTab: HomeTab = HomeTab.ACTIVE
) {
    val canAddMore: Boolean get() = settings.canAddApplication(activeCount)
    val remainingSlots: Int get() = settings.getRemainingSlots(activeCount)
}

enum class HomeTab {
    ACTIVE, ARCHIVED
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ApplicationRepository,
    private val entitlementProvider: EntitlementProvider
) : ViewModel() {
    
    private val _selectedTab = MutableStateFlow(HomeTab.ACTIVE)
    
    val uiState: StateFlow<HomeUiState> = combine(
        repository.getActiveApplications(),
        repository.getArchivedApplications(),
        repository.getActiveApplicationCount(),
        entitlementProvider.getUserSettings(),
        _selectedTab
    ) { active, archived, count, settings, tab ->
        HomeUiState(
            applications = if (tab == HomeTab.ACTIVE) active else archived,
            isLoading = false,
            activeCount = count,
            settings = settings,
            selectedTab = tab
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
    
    fun selectTab(tab: HomeTab) {
        _selectedTab.value = tab
    }
    
    fun archiveApplication(id: String) {
        viewModelScope.launch {
            repository.archiveApplication(id)
        }
    }
    
    fun unarchiveApplication(id: String) {
        viewModelScope.launch {
            repository.unarchiveApplication(id)
        }
    }
    
    fun deleteApplication(id: String) {
        viewModelScope.launch {
            repository.deleteApplication(id)
        }
    }
}

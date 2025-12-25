package com.seek.app.ui.screen.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seek.app.data.entitlement.EntitlementProvider
import com.seek.app.data.repository.ApplicationRepository
import com.seek.app.domain.model.Application
import com.seek.app.domain.model.ApplicationStatus
import com.seek.app.domain.model.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val researchAssistanceEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val activeCount: Int = 0,
    val maxSlots: Int = 3,
    val isSubscribed: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: ApplicationRepository,
    private val entitlementProvider: EntitlementProvider
) : ViewModel() {
    
    val uiState: StateFlow<SettingsUiState> = combine(
        entitlementProvider.getUserSettings(),
        repository.getActiveApplicationCount()
    ) { settings, count ->
        SettingsUiState(
            researchAssistanceEnabled = settings.researchAssistanceEnabled,
            notificationsEnabled = settings.notificationsEnabled,
            activeCount = count,
            maxSlots = settings.getMaxActiveSlots(),
            isSubscribed = settings.subscriptionActive
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )
    
    fun setResearchAssistanceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = entitlementProvider.getUserSettings().first()
            entitlementProvider.updateSettings(current.copy(researchAssistanceEnabled = enabled))
        }
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = entitlementProvider.getUserSettings().first()
            entitlementProvider.updateSettings(current.copy(notificationsEnabled = enabled))
        }
    }
    
    fun wipeAllData() {
        viewModelScope.launch {
            repository.wipeAllData()
            // Reset settings
            entitlementProvider.updateSettings(UserSettings())
        }
    }
    
    suspend fun exportToJson(context: Context, uri: Uri) {
        val json = repository.exportToJson()
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(json.toByteArray())
        }
    }
    
    suspend fun exportToCsv(context: Context, uri: Uri) {
        val csv = repository.exportToCsv()
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(csv.toByteArray())
        }
    }
    
    fun generateSampleData() {
        viewModelScope.launch {
            val sampleApps = listOf(
                Application(
                    companyName = "Google",
                    roleTitle = "Senior Android Developer",
                    location = "Bangalore, India",
                    status = ApplicationStatus.INTERVIEWING
                ),
                Application(
                    companyName = "Microsoft",
                    roleTitle = "Software Engineer",
                    location = "Hyderabad, India",
                    status = ApplicationStatus.APPLIED
                ),
                Application(
                    companyName = "Amazon",
                    roleTitle = "SDE II",
                    location = "Bangalore, India",
                    status = ApplicationStatus.NO_RESPONSE,
                    appliedDate = System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000L)
                )
            )
            
            sampleApps.forEach { app ->
                repository.addApplication(app)
            }
        }
    }
}

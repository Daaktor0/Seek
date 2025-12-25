package com.seek.app.ui.screen.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seek.app.data.entitlement.EntitlementProvider
import com.seek.app.data.repository.ApplicationRepository
import com.seek.app.domain.model.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddApplicationUiState(
    val inputType: String = "manual",
    val companyName: String = "",
    val roleTitle: String = "",
    val jobLink: String = "",
    val location: String = "",
    val notes: String = "",
    val screenshotUri: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val canAdd: Boolean = true,
    val savedApplicationId: String? = null
) {
    val isValid: Boolean get() = companyName.isNotBlank() && roleTitle.isNotBlank()
}

@HiltViewModel
class AddApplicationViewModel @Inject constructor(
    private val repository: ApplicationRepository,
    private val entitlementProvider: EntitlementProvider
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddApplicationUiState())
    val uiState: StateFlow<AddApplicationUiState> = _uiState.asStateFlow()
    
    init {
        checkEntitlement()
    }
    
    private fun checkEntitlement() {
        viewModelScope.launch {
            val settings = entitlementProvider.getUserSettings().first()
            val activeCount = repository.getActiveApplicationCount().first()
            _uiState.value = _uiState.value.copy(
                canAdd = settings.canAddApplication(activeCount)
            )
        }
    }
    
    fun setInputType(type: String) {
        _uiState.value = _uiState.value.copy(inputType = type)
    }
    
    fun updateCompanyName(value: String) {
        _uiState.value = _uiState.value.copy(companyName = value, error = null)
    }
    
    fun updateRoleTitle(value: String) {
        _uiState.value = _uiState.value.copy(roleTitle = value, error = null)
    }
    
    fun updateJobLink(value: String) {
        _uiState.value = _uiState.value.copy(jobLink = value, error = null)
    }
    
    fun updateLocation(value: String) {
        _uiState.value = _uiState.value.copy(location = value, error = null)
    }
    
    fun updateNotes(value: String) {
        _uiState.value = _uiState.value.copy(notes = value, error = null)
    }
    
    fun setScreenshotUri(uri: String?) {
        _uiState.value = _uiState.value.copy(screenshotUri = uri)
        // TODO: In Phase 2, extract data from screenshot using AI
        // For now, user manually enters/confirms data
    }
    
    fun extractFromLink() {
        // TODO: In Phase 2, extract data from job link using AI
        // For now, user manually enters data after pasting link
    }
    
    fun saveApplication() {
        val state = _uiState.value
        
        if (!state.isValid) {
            _uiState.value = state.copy(error = "Company name and role title are required")
            return
        }
        
        if (!state.canAdd) {
            _uiState.value = state.copy(error = "Upgrade to add more applications")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            
            try {
                val application = Application(
                    companyName = state.companyName.trim(),
                    roleTitle = state.roleTitle.trim(),
                    jobLink = state.jobLink.takeIf { it.isNotBlank() }?.trim(),
                    location = state.location.takeIf { it.isNotBlank() }?.trim(),
                    notes = state.notes.takeIf { it.isNotBlank() }?.trim()
                )
                
                val id = repository.addApplication(application)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    savedApplicationId = id
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save application. Please try again."
                )
            }
        }
    }
}

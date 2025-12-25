package com.seek.app.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seek.app.data.repository.ApplicationRepository
import com.seek.app.domain.model.Application
import com.seek.app.domain.model.ApplicationStatus
import com.seek.app.domain.model.Milestone
import com.seek.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ApplicationDetailUiState(
    val application: Application? = null,
    val milestones: List<Milestone> = emptyList(),
    val isLoading: Boolean = true,
    val showStatusPicker: Boolean = false,
    val showAddMilestone: Boolean = false,
    val newMilestoneTitle: String = ""
)

@HiltViewModel
class ApplicationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ApplicationRepository
) : ViewModel() {
    
    private val applicationId: String = checkNotNull(savedStateHandle[Screen.ApplicationDetail.APPLICATION_ID_ARG])
    
    private val _showStatusPicker = MutableStateFlow(false)
    private val _showAddMilestone = MutableStateFlow(false)
    private val _newMilestoneTitle = MutableStateFlow("")
    
    val uiState: StateFlow<ApplicationDetailUiState> = combine(
        repository.getApplicationById(applicationId),
        repository.getMilestonesForApplication(applicationId),
        _showStatusPicker,
        _showAddMilestone,
        _newMilestoneTitle
    ) { application, milestones, showStatus, showAdd, newTitle ->
        ApplicationDetailUiState(
            application = application,
            milestones = milestones,
            isLoading = false,
            showStatusPicker = showStatus,
            showAddMilestone = showAdd,
            newMilestoneTitle = newTitle
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ApplicationDetailUiState()
    )
    
    fun toggleNotifications() {
        viewModelScope.launch {
            uiState.value.application?.let { app ->
                repository.setNotificationsEnabled(app.id, !app.notificationsEnabled)
            }
        }
    }
    
    fun showStatusPicker() {
        _showStatusPicker.value = true
    }
    
    fun hideStatusPicker() {
        _showStatusPicker.value = false
    }
    
    fun updateStatus(status: ApplicationStatus) {
        viewModelScope.launch {
            uiState.value.application?.let { app ->
                repository.updateApplication(app.copy(status = status))
            }
            _showStatusPicker.value = false
        }
    }
    
    fun archiveApplication() {
        viewModelScope.launch {
            repository.archiveApplication(applicationId)
        }
    }
    
    fun unarchiveApplication() {
        viewModelScope.launch {
            repository.unarchiveApplication(applicationId)
        }
    }
    
    fun completeMilestone(milestoneId: String) {
        viewModelScope.launch {
            repository.completeMilestone(milestoneId)
        }
    }
    
    fun setPrimaryMilestone(milestoneId: String) {
        viewModelScope.launch {
            repository.setPrimaryMilestone(applicationId, milestoneId)
        }
    }
    
    fun showAddMilestone() {
        _showAddMilestone.value = true
    }
    
    fun hideAddMilestone() {
        _showAddMilestone.value = false
        _newMilestoneTitle.value = ""
    }
    
    fun updateNewMilestoneTitle(title: String) {
        _newMilestoneTitle.value = title
    }
    
    fun addMilestone() {
        val title = _newMilestoneTitle.value.trim()
        if (title.isBlank()) return
        
        viewModelScope.launch {
            val milestone = Milestone(
                id = UUID.randomUUID().toString(),
                applicationId = applicationId,
                title = title,
                order = uiState.value.milestones.size
            )
            repository.addMilestone(milestone)
            hideAddMilestone()
        }
    }
}

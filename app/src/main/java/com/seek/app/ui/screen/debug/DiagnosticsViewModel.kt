package com.seek.app.ui.screen.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seek.app.data.database.ReminderDao
import com.seek.app.data.model.ReminderEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiagnosticsUiState(
    val pendingCount: Int = 0,
    val activeCount: Int = 0,
    val nextScheduledTime: Long? = null,
    val activeReminders: List<ReminderEntity> = emptyList()
)

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    private val reminderDao: ReminderDao
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DiagnosticsUiState())
    val uiState: StateFlow<DiagnosticsUiState> = _uiState.asStateFlow()
    
    init {
        loadDiagnostics()
    }
    
    private fun loadDiagnostics() {
        viewModelScope.launch {
            reminderDao.getActiveReminders().collect { reminders ->
                val now = System.currentTimeMillis()
                val pending = reminders.filter { 
                    it.scheduledTime <= now && 
                    (it.snoozeUntil == null || it.snoozeUntil <= now)
                }
                val nextTime = reminders
                    .filter { it.scheduledTime > now || (it.snoozeUntil != null && it.snoozeUntil > now) }
                    .minOfOrNull { it.snoozeUntil ?: it.scheduledTime }
                
                _uiState.value = DiagnosticsUiState(
                    pendingCount = pending.size,
                    activeCount = reminders.size,
                    nextScheduledTime = nextTime,
                    activeReminders = reminders.sortedBy { it.scheduledTime }
                )
            }
        }
    }
}

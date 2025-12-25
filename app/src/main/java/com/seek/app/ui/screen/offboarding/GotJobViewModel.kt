package com.seek.app.ui.screen.offboarding

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seek.app.data.entitlement.EntitlementProvider
import com.seek.app.data.repository.ApplicationRepository
import com.seek.app.domain.model.Application
import com.seek.app.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GotJobUiState(
    val application: Application? = null,
    val isSubscribed: Boolean = false
)

@HiltViewModel
class GotJobViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ApplicationRepository,
    private val entitlementProvider: EntitlementProvider
) : ViewModel() {
    
    private val applicationId: String = checkNotNull(savedStateHandle[Screen.GotJob.APPLICATION_ID_ARG])
    
    val uiState: StateFlow<GotJobUiState> = combine(
        repository.getApplicationById(applicationId),
        entitlementProvider.getUserSettings()
    ) { application, settings ->
        GotJobUiState(
            application = application,
            isSubscribed = settings.subscriptionActive
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = GotJobUiState()
    )
    
    suspend fun exportToJson(context: Context, uri: Uri) {
        val json = repository.exportToJson()
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(json.toByteArray())
        }
    }
    
    fun archiveAllApplications() {
        viewModelScope.launch {
            val apps = repository.getActiveApplications().first()
            apps.forEach { app ->
                repository.archiveApplication(app.id)
            }
        }
    }
}

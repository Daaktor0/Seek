package com.seek.app.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seek.app.domain.model.Application
import com.seek.app.domain.model.ApplicationStatus
import com.seek.app.domain.model.Milestone
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationDetailScreen(
    applicationId: String,
    onNavigateBack: () -> Unit,
    onGotJob: () -> Unit,
    viewModel: ApplicationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showMenu by remember { mutableStateOf(false) }
    
    val application = uiState.application
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(application?.companyName ?: "Application") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleNotifications) {
                        Icon(
                            imageVector = if (application?.notificationsEnabled == true) 
                                Icons.Default.Notifications 
                            else 
                                Icons.Default.NotificationsOff,
                            contentDescription = "Toggle notifications"
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options"
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            if (application?.isArchived == true) {
                                DropdownMenuItem(
                                    text = { Text("Unarchive") },
                                    onClick = {
                                        viewModel.unarchiveApplication()
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Unarchive, null)
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Archive") },
                                    onClick = {
                                        viewModel.archiveApplication()
                                        showMenu = false
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Archive, null)
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("I got this job!") },
                                onClick = {
                                    showMenu = false
                                    viewModel.updateStatus(ApplicationStatus.ACCEPTED)
                                    onGotJob()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.WorkspacePremium, null)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (application == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    ApplicationHeader(
                        application = application,
                        onStatusClick = viewModel::showStatusPicker
                    )
                }
                
                item {
                    NextActionCard(application = application)
                }
                
                item {
                    MilestonesSection(
                        milestones = uiState.milestones,
                        onComplete = viewModel::completeMilestone,
                        onSetPrimary = viewModel::setPrimaryMilestone,
                        onAddClick = viewModel::showAddMilestone
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
    
    // Status picker bottom sheet
    if (uiState.showStatusPicker) {
        ModalBottomSheet(
            onDismissRequest = viewModel::hideStatusPicker,
            sheetState = rememberModalBottomSheetState()
        ) {
            StatusPickerSheet(
                currentStatus = application?.status ?: ApplicationStatus.APPLIED,
                onStatusSelected = viewModel::updateStatus
            )
        }
    }
    
    // Add milestone dialog
    if (uiState.showAddMilestone) {
        AlertDialog(
            onDismissRequest = viewModel::hideAddMilestone,
            title = { Text("Add Milestone") },
            text = {
                OutlinedTextField(
                    value = uiState.newMilestoneTitle,
                    onValueChange = viewModel::updateNewMilestoneTitle,
                    label = { Text("What's next?") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = viewModel::addMilestone,
                    enabled = uiState.newMilestoneTitle.isNotBlank()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideAddMilestone) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ApplicationHeader(
    application: Application,
    onStatusClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = application.roleTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = application.companyName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            application.location?.let { location ->
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status chip (clickable)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable(onClick = onStatusClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = application.status.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Applied date
                Text(
                    text = "Applied ${formatDate(application.appliedDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            application.jobLink?.let { link ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = link,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun NextActionCard(application: Application) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Your next step",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = application.getNextActionText(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun MilestonesSection(
    milestones: List<Milestone>,
    onComplete: (String) -> Unit,
    onSetPrimary: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Milestones",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add milestone"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        milestones.forEach { milestone ->
            MilestoneItem(
                milestone = milestone,
                onComplete = { onComplete(milestone.id) },
                onSetPrimary = { onSetPrimary(milestone.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MilestoneItem(
    milestone: Milestone,
    onComplete: () -> Unit,
    onSetPrimary: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (milestone.isPrimary) 
                    MaterialTheme.colorScheme.surfaceVariant 
                else 
                    MaterialTheme.colorScheme.surface
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onComplete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = if (milestone.isCompleted) 
                    Icons.Default.CheckCircle 
                else 
                    Icons.Default.RadioButtonUnchecked,
                contentDescription = if (milestone.isCompleted) "Completed" else "Mark complete",
                tint = if (milestone.isCompleted) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = milestone.title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (milestone.isCompleted) 
                    MaterialTheme.colorScheme.onSurfaceVariant 
                else 
                    MaterialTheme.colorScheme.onSurface
            )
            milestone.description?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (!milestone.isCompleted && !milestone.isPrimary) {
            IconButton(
                onClick = onSetPrimary,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Set as primary",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (milestone.isPrimary && !milestone.isCompleted) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Primary milestone",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StatusPickerSheet(
    currentStatus: ApplicationStatus,
    onStatusSelected: (ApplicationStatus) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Update Status",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        ApplicationStatus.entries.filter { it != ApplicationStatus.ARCHIVED }.forEach { status ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onStatusSelected(status) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (status == currentStatus) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.outline
                        )
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = status.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (status == currentStatus) FontWeight.Medium else FontWeight.Normal
                    )
                    Text(
                        text = status.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

private fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

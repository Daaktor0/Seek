package com.seek.app.data.repository

import com.seek.app.data.database.ApplicationDao
import com.seek.app.data.database.MilestoneDao
import com.seek.app.data.database.ReminderDao
import com.seek.app.data.model.ApplicationEntity
import com.seek.app.data.model.MilestoneEntity
import com.seek.app.domain.model.Application
import com.seek.app.domain.model.Milestone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationRepositoryImpl @Inject constructor(
    private val applicationDao: ApplicationDao,
    private val milestoneDao: MilestoneDao,
    private val reminderDao: ReminderDao
) : ApplicationRepository {
    
    private val json = Json { 
        prettyPrint = true 
        ignoreUnknownKeys = true
    }
    
    override fun getAllApplications(): Flow<List<Application>> {
        return applicationDao.getAllApplications().map { entities ->
            entities.map { entity ->
                val milestones = milestoneDao.getMilestonesForApplicationOnce(entity.id)
                    .map { it.toDomainModel() }
                entity.toDomainModel(milestones)
            }
        }
    }
    
    override fun getActiveApplications(): Flow<List<Application>> {
        return applicationDao.getActiveApplications().map { entities ->
            entities.map { entity ->
                val milestones = milestoneDao.getMilestonesForApplicationOnce(entity.id)
                    .map { it.toDomainModel() }
                entity.toDomainModel(milestones)
            }
        }
    }
    
    override fun getArchivedApplications(): Flow<List<Application>> {
        return applicationDao.getArchivedApplications().map { entities ->
            entities.map { entity ->
                val milestones = milestoneDao.getMilestonesForApplicationOnce(entity.id)
                    .map { it.toDomainModel() }
                entity.toDomainModel(milestones)
            }
        }
    }
    
    override fun getApplicationById(id: String): Flow<Application?> {
        return combine(
            applicationDao.getApplicationById(id),
            milestoneDao.getMilestonesForApplication(id)
        ) { entity, milestoneEntities ->
            entity?.toDomainModel(milestoneEntities.map { it.toDomainModel() })
        }
    }
    
    override fun getActiveApplicationCount(): Flow<Int> {
        return applicationDao.getActiveApplicationCount()
    }
    
    override suspend fun addApplication(application: Application): String {
        val entity = ApplicationEntity.fromDomainModel(application)
        applicationDao.insertApplication(entity)
        
        // Add default milestones
        val defaultMilestones = Milestone.defaultMilestonesFor(application.id)
        milestoneDao.insertMilestones(
            defaultMilestones.map { MilestoneEntity.fromDomainModel(it) }
        )
        
        return application.id
    }
    
    override suspend fun updateApplication(application: Application) {
        applicationDao.updateApplication(ApplicationEntity.fromDomainModel(application))
    }
    
    override suspend fun deleteApplication(id: String) {
        // Milestones and reminders are deleted via CASCADE
        applicationDao.deleteApplicationById(id)
    }
    
    override suspend fun archiveApplication(id: String) {
        applicationDao.archiveApplication(id)
    }
    
    override suspend fun unarchiveApplication(id: String) {
        applicationDao.unarchiveApplication(id)
    }
    
    override suspend fun setNotificationsEnabled(id: String, enabled: Boolean) {
        applicationDao.updateNotificationsEnabled(id, enabled)
    }
    
    override fun getMilestonesForApplication(applicationId: String): Flow<List<Milestone>> {
        return milestoneDao.getMilestonesForApplication(applicationId)
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    override suspend fun addMilestone(milestone: Milestone) {
        milestoneDao.insertMilestone(MilestoneEntity.fromDomainModel(milestone))
    }
    
    override suspend fun updateMilestone(milestone: Milestone) {
        milestoneDao.updateMilestone(MilestoneEntity.fromDomainModel(milestone))
    }
    
    override suspend fun setPrimaryMilestone(applicationId: String, milestoneId: String) {
        milestoneDao.clearPrimaryForApplication(applicationId)
        milestoneDao.setPrimary(milestoneId)
    }
    
    override suspend fun completeMilestone(milestoneId: String) {
        milestoneDao.setCompleted(milestoneId, true)
        
        // Find next incomplete milestone and make it primary
        val milestone = milestoneDao.getMilestoneById(milestoneId) ?: return
        val milestones = milestoneDao.getMilestonesForApplicationOnce(milestone.applicationId)
        val nextMilestone = milestones
            .filter { !it.isCompleted && it.id != milestoneId }
            .minByOrNull { it.order }
        
        if (nextMilestone != null) {
            milestoneDao.clearPrimaryForApplication(milestone.applicationId)
            milestoneDao.setPrimary(nextMilestone.id)
        }
    }
    
    override suspend fun wipeAllData() {
        reminderDao.deleteAllReminders()
        milestoneDao.deleteAllMilestones()
        applicationDao.deleteAllApplications()
    }
    
    override suspend fun exportToJson(): String {
        val applications = mutableListOf<ExportApplication>()
        
        // We need to get all applications synchronously for export
        applicationDao.getAllApplications().collect { entities ->
            for (entity in entities) {
                val milestones = milestoneDao.getMilestonesForApplicationOnce(entity.id)
                applications.add(
                    ExportApplication(
                        companyName = entity.companyName,
                        roleTitle = entity.roleTitle,
                        jobLink = entity.jobLink,
                        location = entity.location,
                        appliedDate = entity.appliedDate,
                        notes = entity.notes,
                        status = entity.status,
                        isArchived = entity.isArchived,
                        milestones = milestones.map { 
                            ExportMilestone(
                                title = it.title,
                                description = it.description,
                                dueDate = it.dueDate,
                                isCompleted = it.isCompleted
                            )
                        }
                    )
                )
            }
            return@collect // Only collect once
        }
        
        return json.encodeToString(ExportData(applications = applications))
    }
    
    override suspend fun exportToCsv(): String {
        val sb = StringBuilder()
        sb.appendLine("Company Name,Role Title,Job Link,Location,Applied Date,Status,Is Archived,Notes")
        
        applicationDao.getAllApplications().collect { entities ->
            for (entity in entities) {
                sb.appendLine(
                    listOf(
                        entity.companyName.escapeCsv(),
                        entity.roleTitle.escapeCsv(),
                        (entity.jobLink ?: "").escapeCsv(),
                        (entity.location ?: "").escapeCsv(),
                        entity.appliedDate.toString(),
                        entity.status.escapeCsv(),
                        entity.isArchived.toString(),
                        (entity.notes ?: "").escapeCsv()
                    ).joinToString(",")
                )
            }
            return@collect // Only collect once
        }
        
        return sb.toString()
    }
    
    private fun String.escapeCsv(): String {
        return if (this.contains(",") || this.contains("\"") || this.contains("\n")) {
            "\"${this.replace("\"", "\"\"")}\""
        } else {
            this
        }
    }
}

@Serializable
private data class ExportData(
    val exportedAt: Long = System.currentTimeMillis(),
    val applications: List<ExportApplication>
)

@Serializable
private data class ExportApplication(
    val companyName: String,
    val roleTitle: String,
    val jobLink: String?,
    val location: String?,
    val appliedDate: Long,
    val notes: String?,
    val status: String,
    val isArchived: Boolean,
    val milestones: List<ExportMilestone>
)

@Serializable
private data class ExportMilestone(
    val title: String,
    val description: String?,
    val dueDate: Long?,
    val isCompleted: Boolean
)

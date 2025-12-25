package com.seek.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.seek.app.domain.model.Milestone

/**
 * Room entity for milestones/next steps.
 * Maps to the 'milestones' table with foreign key to applications.
 */
@Entity(
    tableName = "milestones",
    foreignKeys = [
        ForeignKey(
            entity = ApplicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["applicationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["applicationId"])]
)
data class MilestoneEntity(
    @PrimaryKey
    val id: String,
    val applicationId: String,
    val title: String,
    val description: String?,
    val dueDate: Long?,
    val isCompleted: Boolean,
    val isPrimary: Boolean,
    val createdAt: Long,
    val order: Int
) {
    /**
     * Maps this entity to the domain model.
     */
    fun toDomainModel(): Milestone {
        return Milestone(
            id = id,
            applicationId = applicationId,
            title = title,
            description = description,
            dueDate = dueDate,
            isCompleted = isCompleted,
            isPrimary = isPrimary,
            createdAt = createdAt,
            order = order
        )
    }

    companion object {
        /**
         * Creates an entity from the domain model.
         */
        fun fromDomainModel(milestone: Milestone): MilestoneEntity {
            return MilestoneEntity(
                id = milestone.id,
                applicationId = milestone.applicationId,
                title = milestone.title,
                description = milestone.description,
                dueDate = milestone.dueDate,
                isCompleted = milestone.isCompleted,
                isPrimary = milestone.isPrimary,
                createdAt = milestone.createdAt,
                order = milestone.order
            )
        }
    }
}

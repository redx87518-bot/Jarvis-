package com.jarvis.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jarvis.core.tasks.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val goal: String,
    val status: TaskStatus,
    val createdAt: Long,
    val startedAt: Long?,
    val completedAt: Long?,
    val lastObservation: String,
    val failedAction: String,
    val retryCount: Int,
    val maxRetries: Int,
    val requiredPermissions: List<String>,
)

package com.jarvis.core.tasks

import com.jarvis.core.intent.PermissionLevel
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val goal: String = "",
    val steps: List<TaskStep> = emptyList(),
    val status: TaskStatus = TaskStatus.CREATED,
    val createdAt: Long = System.currentTimeMillis(),
    val startedAt: Long? = null,
    val completedAt: Long? = null,
    val lastObservation: String = "",
    val failureHistory: List<FailureRecord> = emptyList(),
    val failedAction: String = "",
    val pendingActions: List<String> = emptyList(),
    val requiredPermissions: List<PermissionLevel> = emptyList(),
    val retryCount: Int = 0,
    val maxRetries: Int = 3,
    val assignee: String = "",
)

@Serializable
data class TaskStep(
    val id: String,
    val name: String,
    val description: String = "",
    val status: TaskStatus = TaskStatus.CREATED,
    val result: String = "",
)

@Serializable
data class FailureRecord(
    val timestamp: Long,
    val stepId: String,
    val error: String,
    val recoveryAttempted: Boolean = false,
)

@Serializable
enum class TaskStatus {
    CREATED,
    PLANNING,
    RUNNING,
    WAITING_FOR_USER,
    VERIFYING,
    RECOVERING,
    COMPLETED,
    FAILED,
    CANCELLED,
}

val Task.isActive: Boolean
    get() = status in listOf(
        TaskStatus.CREATED, TaskStatus.PLANNING, TaskStatus.RUNNING,
        TaskStatus.WAITING_FOR_USER, TaskStatus.VERIFYING, TaskStatus.RECOVERING,
    )

val Task.isTerminal: Boolean
    get() = status in listOf(TaskStatus.COMPLETED, TaskStatus.FAILED, TaskStatus.CANCELLED)

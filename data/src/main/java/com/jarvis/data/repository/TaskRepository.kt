package com.jarvis.data.repository

import com.jarvis.core.tasks.Task
import com.jarvis.core.tasks.TaskFilter
import com.jarvis.core.tasks.TaskStatus
import com.jarvis.data.dao.TaskDao
import com.jarvis.data.entities.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
) {
    val allTasks: Flow<List<Task>> = taskDao.getAll()
        .map { list -> list.map { it.toDomain() } }

    fun getTasks(filter: TaskFilter): Flow<List<Task>> {
        val statuses = when (filter) {
            TaskFilter.ALL -> emptyList()
            TaskFilter.ACTIVE -> listOf(
                TaskStatus.RUNNING, TaskStatus.PLANNING, TaskStatus.EXECUTING,
                TaskStatus.VERIFYING, TaskStatus.RECOVERING, TaskStatus.CREATED,
            )
            TaskFilter.COMPLETED -> listOf(TaskStatus.COMPLETED)
            TaskFilter.FAILED -> listOf(TaskStatus.FAILED)
            TaskFilter.CANCELLED -> listOf(TaskStatus.CANCELLED)
            TaskFilter.WAITING -> listOf(TaskStatus.WAITING_FOR_USER)
        }
        return if (statuses.isEmpty()) {
            taskDao.getAll().map { list -> list.map { it.toDomain() } }
        } else {
            taskDao.getByStatus(statuses).map { list -> list.map { it.toDomain() } }
        }
    }

    suspend fun getTask(id: String): Task? = taskDao.getById(id)?.toDomain()

    suspend fun saveTask(task: Task) {
        taskDao.insert(task.toEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
    }

    suspend fun updateStatus(id: String, status: TaskStatus) {
        val completedAt = if (status == TaskStatus.COMPLETED) System.currentTimeMillis() else null
        taskDao.updateStatus(id, status, completedAt)
    }

    suspend fun deleteTask(id: String) {
        taskDao.getById(id)?.let { taskDao.delete(it) }
    }

    suspend fun deleteCompleted() = taskDao.deleteCompleted()
}

private fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    goal = goal,
    status = status,
    createdAt = createdAt,
    startedAt = startedAt,
    completedAt = completedAt,
    lastObservation = lastObservation,
    failedAction = failedAction,
    retryCount = retryCount,
    maxRetries = maxRetries,
    requiredPermissions = requiredPermissions.map { com.jarvis.core.intent.PermissionLevel.valueOf(it) },
)

private fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    goal = goal,
    status = status,
    createdAt = createdAt,
    startedAt = startedAt,
    completedAt = completedAt,
    lastObservation = lastObservation,
    failedAction = failedAction,
    retryCount = retryCount,
    maxRetries = maxRetries,
    requiredPermissions = requiredPermissions.map { it.name },
)

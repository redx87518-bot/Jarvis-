package com.jarvis.core.tasks

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface TaskManager {
    suspend fun createTask(task: Task): Task
    suspend fun updateTask(task: Task): Task
    suspend fun getTask(id: String): Task?
    suspend fun listTasks(filter: TaskFilter = TaskFilter.ALL): List<Task>
    suspend fun deleteTask(id: String)
    suspend fun cancelTask(id: String)
    suspend fun pauseTask(id: String): Task
    suspend fun resumeTask(id: String): Task
    suspend fun observeTasks(): Flow<List<Task>>
    suspend fun observeTask(id: String): Flow<Task?>
}

enum class TaskFilter {
    ALL, ACTIVE, COMPLETED, FAILED, CANCELLED, WAITING;
}

class InMemoryTaskManager : TaskManager {
    private val store = kotlinx.coroutines.flow.MutableStateFlow<Map<String, Task>>(emptyMap())

    override suspend fun createTask(task: Task): Task {
        val t = task.copy(id = if (task.id.isBlank()) java.util.UUID.randomUUID().toString() else task.id)
        store.value = store.value + (t.id to t)
        return t
    }

    override suspend fun updateTask(task: Task): Task {
        store.value = store.value + (task.id to task)
        return task
    }

    override suspend fun getTask(id: String): Task? = store.value[id]

    override suspend fun listTasks(filter: TaskFilter): List<Task> {
        val all = store.value.values.toList()
        return when (filter) {
            TaskFilter.ALL -> all
            TaskFilter.ACTIVE -> all.filter { it.isActive }
            TaskFilter.COMPLETED -> all.filter { it.status == com.jarvis.core.tasks.TaskStatus.COMPLETED }
            TaskFilter.FAILED -> all.filter { it.status == com.jarvis.core.tasks.TaskStatus.FAILED }
            TaskFilter.CANCELLED -> all.filter { it.status == com.jarvis.core.tasks.TaskStatus.CANCELLED }
            TaskFilter.WAITING -> all.filter { it.status == com.jarvis.core.tasks.TaskStatus.WAITING_FOR_USER }
        }.sortedByDescending { it.createdAt }
    }

    override suspend fun deleteTask(id: String) {
        store.value = store.value - id
    }

    override suspend fun cancelTask(id: String) {
        val task = store.value[id] ?: return
        store.value = store.value + (id to task.copy(status = TaskStatus.CANCELLED))
    }

    override suspend fun pauseTask(id: String): Task {
        val task = store.value[id] ?: return createTask(Task(id = id, title = "unknown"))
        val paused = task.copy(status = TaskStatus.WAITING_FOR_USER)
        store.value = store.value + (id to paused)
        return paused
    }

    override suspend fun resumeTask(id: String): Task {
        val task = store.value[id] ?: return createTask(Task(id = id, title = "unknown"))
        val resumed = task.copy(status = TaskStatus.RUNNING)
        store.value = store.value + (id to resumed)
        return resumed
    }

    override suspend fun observeTasks(): Flow<List<Task>> = store.map { it.values.toList() }

    override suspend fun observeTask(id: String): Flow<Task?> = store.map { it[id] }
}

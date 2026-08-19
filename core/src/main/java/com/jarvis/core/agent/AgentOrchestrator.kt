package com.jarvis.core.agent

import com.jarvis.core.ai.ModelRouter
import com.jarvis.core.ai.TaskComplexity
import com.jarvis.core.intent.IntentManager
import com.jarvis.core.intent.UserIntent
import com.jarvis.core.tasks.Task
import com.jarvis.core.tasks.TaskManager
import com.jarvis.core.tasks.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AgentState(
    val isRunning: Boolean = false,
    val currentTask: Task? = null,
    val status: AgentStatus = AgentStatus.IDLE,
    val message: String = "",
    val progress: Float = 0f,
)

enum class AgentStatus {
    IDLE,
    LISTENING,
    PROCESSING,
    THINKING,
    PLANNING,
    EXECUTING,
    WAITING_FOR_USER,
    VERIFYING,
    RECOVERING,
    SPEAKING,
    SUCCESS,
    ERROR,
}

data class AgentEvent(
    val type: EventType,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
)

enum class EventType {
    STATE_CHANGED,
    STEP_STARTED,
    STEP_COMPLETED,
    STEP_FAILED,
    CONFIRMATION_REQUESTED,
    USER_ACTION_REQUIRED,
    TASK_COMPLETED,
    TASK_FAILED,
}

interface AgentOrchestrator {
    val state: StateFlow<AgentState>
    suspend fun submitIntent(intent: UserIntent, query: String): Task
    suspend fun cancel(taskId: String)
    suspend fun pause(taskId: String)
    suspend fun resume(taskId: String)
}

interface Tool {
    val name: String
    val description: String
    val requiredPermissions: List<com.jarvis.core.intent.PermissionLevel>
    suspend fun execute(args: Map<String, String>): ToolResult
}

sealed interface ToolResult {
    data class Success(val output: String) : ToolResult
    data class Error(val message: String) : ToolResult
    data class ConfirmationRequired(
        val action: String,
        val payload: String,
    ) : ToolResult
}

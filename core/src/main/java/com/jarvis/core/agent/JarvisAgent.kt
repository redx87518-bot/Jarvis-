package com.jarvis.core.agent

import com.jarvis.core.ai.ModelRouter
import com.jarvis.core.ai.TaskComplexity
import com.jarvis.core.intent.IntentClassification
import com.jarvis.core.intent.IntentManager
import com.jarvis.core.intent.UserIntent
import com.jarvis.core.memory.MemoryStore
import com.jarvis.core.tasks.Task
import com.jarvis.core.tasks.TaskManager
import com.jarvis.core.tasks.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class JarvisAgentOrchestrator(
    private val intentManager: IntentManager,
    private val taskManager: TaskManager,
    private val memoryStore: MemoryStore,
    private val modelRouter: ModelRouter,
    private val toolRegistry: ToolRegistry,
    private val maxRetries: Int = 3,
) : AgentOrchestrator {

    private val _state = MutableStateFlow(AgentState())
    override val state: StateFlow<AgentState> = _state.asStateFlow()

    override suspend fun submitIntent(intent: UserIntent, query: String): Task {
        val task = Task(
            id = UUID.randomUUID().toString(),
            title = query.take(60),
            goal = query,
            requiredPermissions = intent.requires,
            maxRetries = maxRetries,
        )
        val saved = taskManager.createTask(task)
        _state.value = _state.value.copy(
            isRunning = true,
            currentTask = saved,
            status = AgentStatus.PROCESSING,
            message = "Understanding request…",
        )
        return saved
    }

    override suspend fun cancel(taskId: String) {
        taskManager.cancelTask(taskId)
        _state.value = _state.value.copy(
            isRunning = false,
            currentTask = null,
            status = AgentStatus.IDLE,
            message = "Cancelled",
        )
    }

    override suspend fun pause(taskId: String) {
        val task = taskManager.pauseTask(taskId)
        _state.value = _state.value.copy(
            currentTask = task,
            status = AgentStatus.WAITING_FOR_USER,
            isRunning = false,
            message = "Paused — waiting for confirmation",
        )
    }

    override suspend fun resume(taskId: String) {
        val task = taskManager.resumeTask(taskId)
        _state.value = _state.value.copy(
            currentTask = task,
            status = AgentStatus.EXECUTING,
            isRunning = true,
            message = "Resuming task…",
        )
    }

    suspend fun processQuery(query: String): String {
        val classification: IntentClassification = intentManager.classify(query)
        val intent = classification.intent
        return when (intent.type) {
            com.jarvis.core.intent.IntentType.LAUNCH_APP -> "Preparing to launch ${intent.entities["app"]}."
            com.jarvis.core.intent.IntentType.SEARCH_APPS -> "Searching installed apps."
            com.jarvis.core.intent.IntentType.READ_SCREEN -> "Checking the current screen."
            com.jarvis.core.intent.IntentType.READ_NOTIFICATIONS -> "Reading notifications."
            else -> "I'm ready to help with that."
        }
    }
}

class ToolRegistry(private val tools: List<Tool> = emptyList()) {
    fun find(name: String): Tool? = tools.find { it.name == name }
    fun list(): List<Tool> = tools
}

package com.jarvis.launcher.features.agent

import com.jarvis.core.JarvisResult
import com.jarvis.core.ai.ModelProvider
import com.jarvis.core.ai.TaskComplexity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ChatMessageUi(
    val id: String,
    val role: Role,
    val content: String,
    val isStreaming: Boolean = false,
) {
    enum class Role { USER, ASSISTANT }
}

sealed interface AgentUiState {
    object Idle : AgentUiState
    object Listening : AgentUiState
    object Processing : AgentUiState
    object Thinking : AgentUiState
    object Executing : AgentUiState
    object WaitingForConfirmation : AgentUiState
    object Speaking : AgentUiState
    data class Error(val message: String) : AgentUiState
    data class Success(val message: String) : AgentUiState
}

data class ConfirmationRequest(
    val action: String,
    val description: String,
    val payload: String,
)

data class AgentChatUiState(
    val messages: List<ChatMessageUi> = emptyList(),
    val isRecording: Boolean = false,
    val agentState: AgentUiState = AgentUiState.Idle,
    val availableModels: List<ModelProvider> = emptyList(),
    val selectedModel: String = "",
    val confirmation: ConfirmationRequest? = null,
)

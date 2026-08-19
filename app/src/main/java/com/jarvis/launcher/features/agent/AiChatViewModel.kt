package com.jarvis.launcher.features.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.core.JarvisResult
import com.jarvis.core.ai.ModelRouter
import com.jarvis.core.ai.TaskComplexity
import com.jarvis.core.agent.AgentOrchestrator
import com.jarvis.core.intent.IntentManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val agentOrchestrator: AgentOrchestrator,
    private val intentManager: IntentManager,
    private val modelRouter: ModelRouter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgentChatUiState())
    val uiState: StateFlow<AgentChatUiState> = _uiState.asStateFlow()

    val agentState: StateFlow<com.jarvis.launcher.features.agent.AgentUiState> =
        agentOrchestrator.state
            .map { it.status.toUiState() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.jarvis.launcher.features.agent.AgentUiState.Idle)

    init {
        loadModels()
    }

    private fun loadModels() {
        viewModelScope.launch {
            val providers = modelRouter.getAvailableProviders()
            _uiState.value = _uiState.value.copy(
                availableModels = providers,
                selectedModel = providers.firstOrNull()?.id ?: "",
            )
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessageUi(
            id = UUID.randomUUID().toString(),
            role = ChatMessageUi.Role.USER,
            content = text,
        )
        val current = _uiState.value.messages.toMutableList()
        current.add(userMsg)
        _uiState.value = _uiState.value.copy(messages = current, agentState = AgentUiState.Processing)
        _uiState.value = _uiState.value.copy(
            agentState = AgentUiState.Processing,
        )
        processMessage(text)
    }

    private fun processMessage(text: String) {
        viewModelScope.launch {
            val intent = intentManager.parseNaturalLanguage(text)
            val responseText = when (intent.type) {
                com.jarvis.core.intent.IntentType.LAUNCH_APP -> {
                    "I'll open ${intent.entities["app"]} for you."
                }
                com.jarvis.core.intent.IntentType.SEARCH_APPS -> "Searching installed apps."
                com.jarvis.core.intent.IntentType.READ_SCREEN -> "Checking your screen."
                com.jarvis.core.intent.IntentType.READ_NOTIFICATIONS -> "Reading notifications."
                com.jarvis.core.intent.IntentType.SEARCH_WEB -> "Searching the web for that."
                com.jarvis.core.intent.IntentType.APP_INFO -> "Let me explain that app."
                else -> "I understand you want to: $text"
            }
            val botMsg = ChatMessageUi(
                id = UUID.randomUUID().toString(),
                role = ChatMessageUi.Role.ASSISTANT,
                content = responseText,
            )
            val current = _uiState.value.messages.toMutableList()
            current.add(botMsg)
            _uiState.value = _uiState.value.copy(
                messages = current,
                agentState = AgentUiState.Idle,
            )
            agentOrchestrator.submitIntent(intent, text)
        }
    }

    fun setRecording(recording: Boolean) {
        _uiState.value = _uiState.value.copy(isRecording = recording)
    }

    fun stopSpeaking() {
        _uiState.value = _uiState.value.copy(agentState = AgentUiState.Idle)
    }

    fun requestConfirmation(action: String, description: String, payload: String) {
        _uiState.value = _uiState.value.copy(
            confirmation = ConfirmationRequest(action, description, payload),
            agentState = AgentUiState.WaitingForConfirmation,
        )
    }

    fun confirmAction() {
        _uiState.value = _uiState.value.copy(
            confirmation = null,
            agentState = AgentUiState.Executing,
        )
    }

    fun cancelConfirmation() {
        _uiState.value = _uiState.value.copy(
            confirmation = null,
            agentState = AgentUiState.Idle,
        )
    }
}

private fun com.jarvis.core.agent.AgentStatus.toUiState(): AgentUiState = when (this) {
    com.jarvis.core.agent.AgentStatus.IDLE -> AgentUiState.Idle
    com.jarvis.core.agent.AgentStatus.LISTENING -> AgentUiState.Listening
    com.jarvis.core.agent.AgentStatus.PROCESSING -> AgentUiState.Processing
    com.jarvis.core.agent.AgentStatus.THINKING -> AgentUiState.Thinking
    com.jarvis.core.agent.AgentStatus.PLANNING -> AgentUiState.Processing
    com.jarvis.core.agent.AgentStatus.EXECUTING -> AgentUiState.Executing
    com.jarvis.core.agent.AgentStatus.WAITING_FOR_USER -> AgentUiState.WaitingForConfirmation
    com.jarvis.core.agent.AgentStatus.VERIFYING -> AgentUiState.Executing
    com.jarvis.core.agent.AgentStatus.RECOVERING -> AgentUiState.Processing
    com.jarvis.core.agent.AgentStatus.SPEAKING -> AgentUiState.Speaking
    com.jarvis.core.agent.AgentStatus.SUCCESS -> AgentUiState.Idle
    com.jarvis.core.agent.AgentStatus.ERROR -> AgentUiState.Error("")
}

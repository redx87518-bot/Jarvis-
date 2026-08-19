package com.jarvis.core.ai

import com.jarvis.core.ChatMessage
import com.jarvis.core.CompletionResult
import com.jarvis.core.JarvisResult
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

interface ModelProvider {
    val id: String
    val name: String
    val isAvailable: Boolean
    val supportsVision: Boolean
    val supportsStreaming: Boolean
    val maxTokens: Int
    suspend fun complete(
        messages: List<ChatMessage>,
        temperature: Float = 0.7f,
        maxTokens: Int = 2048,
    ): Flow<JarvisResult<CompletionResult>>

    suspend fun completeText(
        messages: List<ChatMessage>,
        temperature: Float = 0.7f,
        maxTokens: Int = 2048,
    ): JarvisResult<String>
}

interface SpeechProvider {
    val id: String
    val name: String
    val isAvailable: Boolean
    suspend fun transcribe(audioBytes: ByteArray, mimeType: String = "audio/wav"): JarvisResult<String>
}

interface TtsProvider {
    val id: String
    val name: String
    val isAvailable: Boolean
    suspend fun speak(text: String, onProgress: ((Float) -> Unit)? = null): JarvisResult<Unit>
    suspend fun stop()
}

enum class ModelRole {
    REASONING,
    VISION,
    SPEECH_TO_TEXT,
    TEXT_TO_SPEECH,
}

@Serializable
data class ModelCapabilities(
    val maxTokens: Int,
    val supportsVision: Boolean,
    val supportsStreaming: Boolean,
    val isLocal: Boolean,
    val requiresApiKey: Boolean,
    val latencyMs: Int,
)

interface ModelRouter {
    data class RouteDecision(
        val provider: ModelProvider,
        val capabilities: ModelCapabilities,
    )

    suspend fun route(
        messages: List<ChatMessage>,
        taskComplexity: TaskComplexity,
        internetAvailable: Boolean,
        deviceMemoryMb: Int,
        privacyMode: Boolean,
        userPreference: String? = null,
    ): JarvisResult<RouteDecision>

    suspend fun getAvailableProviders(): List<ModelProvider>
}

enum class TaskComplexity {
    SIMPLE,
    MODERATE,
    COMPLEX,
    REASONING;

    val isComplex: Boolean get() = this == COMPLEX || this == REASONING
}

data class RoutingConfig(
    val preferLocal: Boolean = false,
    val maxLatencyMs: Int = 5000,
    val maxCostPerToken: Double = Double.MAX_VALUE,
)

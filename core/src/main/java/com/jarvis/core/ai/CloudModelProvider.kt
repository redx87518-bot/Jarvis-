package com.jarvis.core.ai

import com.jarvis.core.ChatMessage
import com.jarvis.core.CompletionResult
import com.jarvis.core.JarvisResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

class LocalModelProvider(
    override val id: String = "local",
    override val name: String = "Local Stub",
    override val maxTokens: Int = 4096,
) : ModelProvider {

    override val isAvailable: Boolean = true
    override val supportsVision: Boolean = false
    override val supportsStreaming: Boolean = false

    override suspend fun complete(
        messages: List<ChatMessage>,
        temperature: Float,
        maxTokens: Int,
    ): Flow<JarvisResult<CompletionResult>> = flow {
        emit(JarvisResult.Error("No local model configured. Install a model in Settings > AI."))
    }

    override suspend fun completeText(
        messages: List<ChatMessage>,
        temperature: Float,
        maxTokens: Int,
    ): JarvisResult<String> = JarvisResult.Error("No local model configured.")
}

data class CloudModelConfig(
    val baseUrl: String,
    val apiKey: String? = null,
    val modelName: String,
    val isVision: Boolean = false,
)

class CloudModelProvider(
    override val id: String,
    override val name: String,
    private val config: CloudModelConfig,
    override val maxTokens: Int = 8192,
    override val supportsVision: Boolean = false,
    override val supportsStreaming: Boolean = true,
) : ModelProvider {

    override val isAvailable: Boolean get() = !config.apiKey.isNullOrEmpty()

    override suspend fun complete(
        messages: List<ChatMessage>,
        temperature: Float,
        maxTokens: Int,
    ): Flow<JarvisResult<CompletionResult>> = flow {
        if (config.apiKey.isNullOrEmpty()) {
            emit(JarvisResult.Error("API key not configured"))
            return@flow
        }
        emit(JarvisResult.Loading())
        try {
            val textResult = CloudApiClient.request(
                baseUrl = config.baseUrl,
                apiKey = config.apiKey,
                modelName = config.modelName,
                messages = messages,
                temperature = temperature,
                maxTokens = maxTokens,
            )
            when (textResult) {
                is JarvisResult.Success -> emit(JarvisResult.Success(CompletionResult(textResult.data)))
                is JarvisResult.Error -> emit(JarvisResult.Error(textResult.message, textResult.cause))
                is JarvisResult.Loading -> emit(JarvisResult.Loading())
            }
        } catch (e: Exception) {
            emit(JarvisResult.Error("Network error: ${e.message}", e))
        }
    }

    override suspend fun completeText(
        messages: List<ChatMessage>,
        temperature: Float,
        maxTokens: Int,
    ): JarvisResult<String> {
        if (config.apiKey.isNullOrEmpty()) {
            return JarvisResult.Error("API key not configured")
        }
        return CloudApiClient.request(
            baseUrl = config.baseUrl,
            apiKey = config.apiKey,
            modelName = config.modelName,
            messages = messages,
            temperature = temperature,
            maxTokens = maxTokens,
        )
    }
}

object CloudApiClient {
    suspend fun request(
        baseUrl: String,
        apiKey: String,
        modelName: String,
        messages: List<ChatMessage>,
        temperature: Float,
        maxTokens: Int,
    ): JarvisResult<String> {
        return JarvisResult.Error("Cloud provider requires network and a valid API key.")
    }
}

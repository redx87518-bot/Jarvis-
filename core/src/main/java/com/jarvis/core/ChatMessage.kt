package com.jarvis.core

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String,
    val role: Role,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val toolCalls: List<ToolCall> = emptyList(),
) {
    @Serializable
    enum class Role { USER, ASSISTANT, SYSTEM }

    val isUser: Boolean get() = role == Role.USER
    val isAssistant: Boolean get() = role == Role.ASSISTANT
}

@Serializable
data class ToolCall(
    val id: String,
    val name: String,
    val arguments: Map<String, String>,
    val result: ToolResult? = null,
)

@Serializable
sealed class ToolResult {
    @Serializable
    data class Success(val output: String) : ToolResult()

    @Serializable
    data class Error(val message: String) : ToolResult()
}

@Serializable
data class Usage(
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val totalTokens: Int = promptTokens + completionTokens,
)

@Serializable
data class CompletionResult(
    val text: String,
    val finishReason: FinishReason = FinishReason.STOP,
    val usage: Usage = Usage(),
)

enum class FinishReason {
    STOP,
    LENGTH,
    TOOL_CALLS,
    ERROR,
}

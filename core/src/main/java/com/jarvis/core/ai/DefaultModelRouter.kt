package com.jarvis.core.ai

import com.jarvis.core.ChatMessage
import com.jarvis.core.JarvisResult
import kotlinx.coroutines.flow.flow

class DefaultModelRouter(
    private val localProvider: LocalModelProvider,
    private val cloudProviders: List<CloudModelProvider>,
) : ModelRouter {

    override suspend fun route(
        messages: List<ChatMessage>,
        taskComplexity: TaskComplexity,
        internetAvailable: Boolean,
        deviceMemoryMb: Int,
        privacyMode: Boolean,
        userPreference: String?,
    ): JarvisResult<ModelRouter.RouteDecision> {
        val providers = getAvailableProviders()
        if (providers.isEmpty()) {
            return JarvisResult.Error("No model providers available.")
        }

        val chosen = if (userPreference != null) {
            providers.find { it.id == userPreference } ?: providers.first()
        } else if (privacyMode || !internetAvailable) {
            localProvider
        } else {
            if (taskComplexity.isComplex && cloudProviders.isNotEmpty()) {
                cloudProviders.first { it.isAvailable }
            } else {
                localProvider
            }
        }

        val caps = ModelCapabilities(
            maxTokens = chosen.maxTokens,
            supportsVision = chosen.supportsVision,
            supportsStreaming = chosen.supportsStreaming,
            isLocal = chosen is LocalModelProvider,
            requiresApiKey = chosen is CloudModelProvider,
            latencyMs = if (chosen is LocalModelProvider) 0 else 800,
        )

        return JarvisResult.Success(ModelRouter.RouteDecision(chosen, caps))
    }

    override suspend fun getAvailableProviders(): List<ModelProvider> {
        val all = mutableListOf<ModelProvider>(localProvider)
        all.addAll(cloudProviders.filter { it.isAvailable })
        return all
    }
}

class StubModelRouter : ModelRouter {
    private val local = LocalModelProvider()

    override suspend fun route(
        messages: List<ChatMessage>,
        taskComplexity: TaskComplexity,
        internetAvailable: Boolean,
        deviceMemoryMb: Int,
        privacyMode: Boolean,
        userPreference: String?,
    ): JarvisResult<ModelRouter.RouteDecision> =
        JarvisResult.Success(
            ModelRouter.RouteDecision(
                provider = local,
                capabilities = ModelCapabilities(
                    maxTokens = 4096,
                    supportsVision = false,
                    supportsStreaming = false,
                    isLocal = true,
                    requiresApiKey = false,
                    latencyMs = 0,
                ),
            )
        )

    override suspend fun getAvailableProviders(): List<ModelProvider> = listOf(local)
}

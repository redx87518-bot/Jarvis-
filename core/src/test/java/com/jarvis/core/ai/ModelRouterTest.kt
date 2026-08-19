package com.jarvis.core.ai

import com.jarvis.core.ChatMessage
import com.jarvis.core.JarvisResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelRouterTest {

    private val router = StubModelRouter()

    @Test
    fun `route returns local provider in privacy mode`() = runTest {
        val messages = listOf(
            ChatMessage(id = "1", role = ChatMessage.Role.SYSTEM, content = "You are JARVIS")
        )
        val result = router.route(
            messages = messages,
            taskComplexity = TaskComplexity.SIMPLE,
            internetAvailable = true,
            deviceMemoryMb = 4096,
            privacyMode = true,
        )
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.capabilities?.isLocal == true)
    }

    @Test
    fun `route returns local provider when offline`() = runTest {
        val messages = emptyList<ChatMessage>()
        val result = router.route(
            messages = messages,
            taskComplexity = TaskComplexity.COMPLEX,
            internetAvailable = false,
            deviceMemoryMb = 4096,
            privacyMode = false,
        )
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.capabilities?.isLocal == true)
    }

    @Test
    fun `getAvailableProviders returns at least local`() = runTest {
        val providers = router.getAvailableProviders()
        assertTrue(providers.isNotEmpty())
        assertTrue(providers.any { it is LocalModelProvider })
    }
}

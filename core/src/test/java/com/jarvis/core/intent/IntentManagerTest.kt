package com.jarvis.core.intent

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class IntentManagerTest {

    private val intentManager = LocalIntentManager()

    @Test
    fun `launch app intent for whatsapp`() = runTest {
        val result = intentManager.classify("open whatsapp")
        assertEquals(IntentType.LAUNCH_APP, result.intent.type)
        assertEquals("whatsapp", result.intent.entities["app"])
    }

    @Test
    fun `read screen intent for what is on my screen`() = runTest {
        val result = intentManager.classify("What's on my screen?")
        assertEquals(IntentType.READ_SCREEN, result.intent.type)
    }

    @Test
    fun `search apps intent`() = runTest {
        val result = intentManager.classify("show me cybersecurity apps")
        assertEquals(IntentType.SEARCH_APPS, result.intent.type)
    }

    @Test
    fun `unknown intent for unrecognized query`() = runTest {
        val result = intentManager.classify("tell me a joke")
        assertEquals(IntentType.UNKNOWN, result.intent.type)
        assertEquals(0.1f, result.intent.confidence, 0.01f)
    }

    @Test
    fun `launch app for generic open command`() = runTest {
        val result = intentManager.classify("open the calculator")
        assertEquals(IntentType.LAUNCH_APP, result.intent.type)
    }
}

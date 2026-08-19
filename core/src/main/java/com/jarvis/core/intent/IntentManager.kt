package com.jarvis.core.intent

import kotlinx.serialization.Serializable

@Serializable
data class UserIntent(
    val type: IntentType,
    val query: String,
    val entities: Map<String, String> = emptyMap(),
    val confidence: Float = 0f,
    val requires: List<PermissionLevel> = emptyList(),
)

enum class IntentType {
    LAUNCH_APP,
    SEARCH_APPS,
    APP_INFO,
    OPEN_SETTINGS,
    READ_SCREEN,
    READ_NOTIFICATIONS,
    SEND_MESSAGE,
    READ_MESSAGE,
    SEARCH_WEB,
    READ_EMAIL,
    OPEN_BROWSER,
    TASK_LIST,
    TASK_RESUME,
    MEMORY_QUERY,
    MEMORY_CLEAR,
    SETTINGS_OPEN,
    VOICE_COMMAND,
    UNKNOWN,
}

enum class PermissionLevel {
    READ,
    NAVIGATE,
    INTERACT,
    COMMUNICATE,
    SENSITIVE,
    HIGH_IMPACT,
}

data class IntentClassification(
    val intent: UserIntent,
    val rawText: String,
    val modelUsed: String,
    val confidence: Float,
)

interface IntentManager {
    suspend fun classify(query: String): IntentClassification
    suspend fun parseNaturalLanguage(query: String): UserIntent
}

class LocalIntentManager : IntentManager {
    override suspend fun classify(query: String): IntentClassification {
        val intent = parseNaturalLanguage(query)
        return IntentClassification(
            intent = intent,
            rawText = query,
            modelUsed = "local-rules",
            confidence = intent.confidence,
        )
    }

    override suspend fun parseNaturalLanguage(query: String): UserIntent {
        val lower = query.lowercase().trim()
        return when {
            lower.contains("open") && lower.contains("whatsapp") ->
                UserIntent(IntentType.LAUNCH_APP, query, mapOf("app" to "whatsapp"), 0.95f)
            lower.contains("open") && lower.contains("gmail") ->
                UserIntent(IntentType.LAUNCH_APP, query, mapOf("app" to "gmail"), 0.95f)
            lower.contains("open") && (lower.contains("browser") || lower.contains("chrome")) ->
                UserIntent(IntentType.LAUNCH_APP, query, mapOf("app" to "chrome"), 0.9f)
            lower.contains("open") && lower.contains("github") ->
                UserIntent(IntentType.LAUNCH_APP, query, mapOf("app" to "github"), 0.9f)
            lower.contains("search") && lower.contains("app") ->
                UserIntent(IntentType.SEARCH_APPS, query, emptyMap(), 0.85f)
            lower.contains("what") && (lower.contains("app") || lower.contains("this app")) ->
                UserIntent(IntentType.APP_INFO, query, emptyMap(), 0.75f)
            lower.contains("what's on my screen") || lower.contains("what is on my screen") ->
                UserIntent(IntentType.READ_SCREEN, query, emptyMap(), 0.9f)
            lower.contains("notification") || lower.contains("message") ->
                UserIntent(IntentType.READ_NOTIFICATIONS, query, emptyMap(), 0.8f)
            lower.contains("read") && lower.contains("email") ->
                UserIntent(IntentType.READ_EMAIL, query, emptyMap(), 0.85f)
            lower.contains("search") || lower.contains("google") ->
                UserIntent(IntentType.SEARCH_WEB, query, emptyMap(), 0.8f)
            lower.contains("task") ->
                UserIntent(IntentType.TASK_LIST, query, emptyMap(), 0.8f)
            lower.contains("settings") ->
                UserIntent(IntentType.SETTINGS_OPEN, query, emptyMap(), 0.7f)
            lower.contains("memory") || lower.contains("remember") ->
                UserIntent(IntentType.MEMORY_QUERY, query, emptyMap(), 0.7f)
            lower.startsWith("open ") -> {
                val appName = lower.removePrefix("open ").trim()
                UserIntent(IntentType.LAUNCH_APP, query, mapOf("app" to appName), 0.6f)
            }
            else ->
                UserIntent(IntentType.UNKNOWN, query, emptyMap(), 0.1f)
        }
    }
}

package com.jarvis.core

import kotlinx.serialization.Serializable

@Serializable
data class AppInfo(
    val packageName: String,
    val appName: String,
    val category: AppCategory = AppCategory.OTHER,
    val isSystemApp: Boolean = false,
    val isEnabled: Boolean = true,
    val lastUsedTime: Long = 0L,
    val installTime: Long = 0L,
    val iconRes: String? = null,
    val description: String? = null,
    val usesInternet: Boolean = false,
)

enum class AppCategory {
    COMMUNICATION,
    PRODUCTIVITY,
    SOCIAL,
    MEDIA,
    UTILITIES,
    FINANCE,
    EDUCATION,
    TRAVEL,
    HEALTH,
    GAMING,
    DEVELOPMENT,
    CYBERSECURITY,
    OTHER;

    fun isRelevantFor(query: String): Boolean {
        val lower = query.lowercase()
        return when (this) {
            COMMUNICATION -> lower.contains("messag") || lower.contains("whatsapp") || lower.contains("telegram") || lower.contains("chat") || lower.contains("call")
            PRODUCTIVITY -> lower.contains("document") || lower.contains("productivity") || lower.contains("work")
            SOCIAL -> lower.contains("social") || lower.contains("network")
            MEDIA -> lower.contains("photo") || lower.contains("video") || lower.contains("media") || lower.contains("music") || lower.contains("camera")
            UTILITIES -> lower.contains("util") || lower.contains("tool")
            FINANCE -> lower.contains("bank") || lower.contains("finance") || lower.contains("pay")
            EDUCATION -> lower.contains("learn") || lower.contains("edu") || lower.contains("study") || lower.contains("cours")
            TRAVEL -> lower.contains("travel") || lower.contains("map") || lower.contains("nav")
            HEALTH -> lower.contains("health") || lower.contains("fit") || lower.contains("med")
            GAMING -> lower.contains("game") || lower.contains("play")
            DEVELOPMENT -> lower.contains("code") || lower.contains("github") || lower.contains("dev") || lower.contains("git")
            CYBERSECURITY -> lower.contains("secur") || lower.contains("cyber") || lower.contains("vpn")
            OTHER -> false
        }
    }
}

@Serializable
data class AppExplanation(
    val appName: String,
    val packageName: String,
    val description: String,
    val mainFunctions: List<String>,
    val category: String,
    val requiresInternet: Boolean,
    val relevantPermissions: List<String>,
    val privacySummary: String,
    val similarApps: List<AppInfo>,
)

package com.jarvis.launcher.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.android.apps.AppManager
import com.jarvis.core.AppInfo
import com.jarvis.core.tasks.TaskManager
import com.jarvis.data.settings.SettingsManager
import com.jarvis.launcher.features.agent.AgentUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val greeting: String = "Good morning",
    val subGreeting: String = "",
    val time: String = "",
    val batteryLevel: Int = 82,
    val favorites: List<AppInfo> = emptyList(),
    val recentApps: List<AppInfo> = emptyList(),
    val briefingItems: List<BriefingItem> = emptyList(),
    val agentStatus: AgentUiState = AgentUiState.Idle,
)

data class BriefingItem(
    val icon: String,
    val text: String,
    val category: String,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appManager: AppManager,
    private val taskManager: TaskManager,
    private val settingsManager: SettingsManager,
) : ViewModel() {

    val uiState = appManager.observeApps()
        .map { apps -> buildUiState(apps) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    private suspend fun buildUiState(apps: List<AppInfo>): HomeUiState {
        val now = System.currentTimeMillis()
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)

        val greeting = when {
            hour < 5 -> "Good night"
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }

        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(now))

        val favorites = apps.filter { it.packageName.startsWith("com.whatsapp") ||
            it.packageName.startsWith("com.google.android.gm") ||
            it.packageName.startsWith("com.android.chrome") ||
            it.packageName.contains("github") }
        val recentApps = apps.sortedByDescending { it.lastUsedTime }
            .take(8)
            .filter { it.lastUsedTime > 0 }

        val briefing = listOf(
            BriefingItem("🔴", "GitHub workflow needs attention", "important"),
            BriefingItem("💬", "2 messages need attention", "important"),
            BriefingItem("📧", "1 important email", "important"),
        )

        return HomeUiState(
            greeting = greeting,
            subGreeting = "Welcome back",
            time = time,
            batteryLevel = 82,
            favorites = favorites,
            recentApps = recentApps,
            briefingItems = briefing,
            agentStatus = AgentUiState.Idle,
        )
    }
}

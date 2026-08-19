package com.jarvis.launcher.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.data.settings.SettingsManager
import com.jarvis.core.JarvisThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: JarvisThemeMode = JarvisThemeMode.System,
    val dynamicColor: Boolean = true,
    val accentColor: String = "#4A90D9",
    val lowMemoryMode: Boolean = false,
    val autoSpeak: Boolean = true,
    val wakeWordEnabled: Boolean = true,
    val aiModelProvider: String = "local",
    val confirmationLevel: Int = 1,
    val memoryEnabled: Boolean = true,
    val maxTaskDurationSec: Int = 120,
    val maxRetries: Int = 3,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
) : ViewModel() {

    val uiState = kotlinx.coroutines.flow.combine(
        settingsManager.themeMode,
        settingsManager.dynamicColor,
        settingsManager.accentColor,
        settingsManager.lowMemoryMode,
        settingsManager.autoSpeak,
        settingsManager.wakeWordEnabled,
        settingsManager.aiModelProvider,
        settingsManager.confirmationLevel,
        settingsManager.memoryEnabled,
        settingsManager.maxTaskDurationSec,
        settingsManager.maxRetries,
    ) { args ->
        SettingsUiState(
            themeMode = args[0] as JarvisThemeMode,
            dynamicColor = args[1] as Boolean,
            accentColor = args[2] as String,
            lowMemoryMode = args[3] as Boolean,
            autoSpeak = args[4] as Boolean,
            wakeWordEnabled = args[5] as Boolean,
            aiModelProvider = args[6] as String,
            confirmationLevel = args[7] as Int,
            memoryEnabled = args[8] as Boolean,
            maxTaskDurationSec = args[9] as Int,
            maxRetries = args[10] as Int,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setThemeMode(mode: JarvisThemeMode) = settingsManager.setThemeMode(mode)
    fun setDynamicColor(enabled: Boolean) = settingsManager.setDynamicColor(enabled)
    fun setAccentColor(color: String) = settingsManager.setAccentColor(color)
    fun setLowMemoryMode(enabled: Boolean) = settingsManager.setLowMemoryMode(enabled)
    fun setAutoSpeak(enabled: Boolean) = settingsManager.setAutoSpeak(enabled)
    fun setWakeWordEnabled(enabled: Boolean) = settingsManager.setWakeWordEnabled(enabled)
    fun setAiModelProvider(provider: String) = settingsManager.setAiModelProvider(provider)
    fun setConfirmationLevel(level: Int) = settingsManager.setConfirmationLevel(level)
    fun setMemoryEnabled(enabled: Boolean) = settingsManager.setMemoryEnabled(enabled)
    fun setMaxTaskDurationSec(seconds: Int) = settingsManager.setMaxTaskDurationSec(seconds)
    fun setMaxRetries(retries: Int) = settingsManager.setMaxRetries(retries)
}

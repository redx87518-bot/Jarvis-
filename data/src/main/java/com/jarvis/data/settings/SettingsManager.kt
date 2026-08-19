package com.jarvis.data.settings

import android.content.Context
import android.content.SharedPreferences
import com.jarvis.core.JarvisThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("jarvis_settings", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(readThemeMode())
    val themeMode: Flow<JarvisThemeMode> = _themeMode.asStateFlow()

    private val _dynamicColor = MutableStateFlow(readDynamicColor())
    val dynamicColor: Flow<Boolean> = _dynamicColor.asStateFlow()

    private val _accentColor = MutableStateFlow(readAccentColor())
    val accentColor: Flow<String> = _accentColor.asStateFlow()

    private val _lowMemoryMode = MutableStateFlow(readLowMemoryMode())
    val lowMemoryMode: Flow<Boolean> = _lowMemoryMode.asStateFlow()

    private val _autoSpeak = MutableStateFlow(readAutoSpeak())
    val autoSpeak: Flow<Boolean> = _autoSpeak.asStateFlow()

    private val _wakeWordEnabled = MutableStateFlow(readWakeWord())
    val wakeWordEnabled: Flow<Boolean> = _wakeWordEnabled.asStateFlow()

    private val _aiModelProvider = MutableStateFlow(readAiModelProvider())
    val aiModelProvider: Flow<String> = _aiModelProvider.asStateFlow()

    private val _confirmationLevel = MutableStateFlow(readConfirmationLevel())
    val confirmationLevel: Flow<Int> = _confirmationLevel.asStateFlow()

    private val _memoryEnabled = MutableStateFlow(readMemoryEnabled())
    val memoryEnabled: Flow<Boolean> = _memoryEnabled.asStateFlow()

    private val _maxTaskDurationSec = MutableStateFlow(readMaxTaskDuration())
    val maxTaskDurationSec: Flow<Int> = _maxTaskDurationSec.asStateFlow()

    private val _maxRetries = MutableStateFlow(readMaxRetries())
    val maxRetries: Flow<Int> = _maxRetries.asStateFlow()

    // Write methods
    fun setThemeMode(mode: JarvisThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode::class.java.simpleName).apply()
    }

    fun setDynamicColor(enabled: Boolean) {
        _dynamicColor.value = enabled
        prefs.edit().putBoolean("dynamic_color", enabled).apply()
    }

    fun setAccentColor(color: String) {
        _accentColor.value = color
        prefs.edit().putString("accent_color", color).apply()
    }

    fun setLowMemoryMode(enabled: Boolean) {
        _lowMemoryMode.value = enabled
        prefs.edit().putBoolean("low_memory_mode", enabled).apply()
    }

    fun setAutoSpeak(enabled: Boolean) {
        _autoSpeak.value = enabled
        prefs.edit().putBoolean("auto_speak", enabled).apply()
    }

    fun setWakeWordEnabled(enabled: Boolean) {
        _wakeWordEnabled.value = enabled
        prefs.edit().putBoolean("wake_word_enabled", enabled).apply()
    }

    fun setAiModelProvider(provider: String) {
        _aiModelProvider.value = provider
        prefs.edit().putString("ai_model_provider", provider).apply()
    }

    fun setConfirmationLevel(level: Int) {
        _confirmationLevel.value = level
        prefs.edit().putInt("confirmation_level", level).apply()
    }

    fun setMemoryEnabled(enabled: Boolean) {
        _memoryEnabled.value = enabled
        prefs.edit().putBoolean("memory_enabled", enabled).apply()
    }

    fun setMaxTaskDurationSec(seconds: Int) {
        _maxTaskDurationSec.value = seconds
        prefs.edit().putInt("max_task_duration_sec", seconds).apply()
    }

    fun setMaxRetries(retries: Int) {
        _maxRetries.value = retries
        prefs.edit().putInt("max_retries", retries).apply()
    }

    // Read helpers
    private fun readThemeMode(): JarvisThemeMode {
        val name = prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM"
        return when (name) {
            "DARK" -> JarvisThemeMode.Dark
            "LIGHT" -> JarvisThemeMode.Light
            else -> JarvisThemeMode.System
        }
    }

    private fun readDynamicColor(): Boolean = prefs.getBoolean("dynamic_color", true)
    private fun readAccentColor(): String = prefs.getString("accent_color", "#4A90D9") ?: "#4A90D9"
    private fun readLowMemoryMode(): Boolean = prefs.getBoolean("low_memory_mode", false)
    private fun readAutoSpeak(): Boolean = prefs.getBoolean("auto_speak", true)
    private fun readWakeWord(): Boolean = prefs.getBoolean("wake_word_enabled", true)
    private fun readAiModelProvider(): String = prefs.getString("ai_model_provider", "local") ?: "local"
    private fun readConfirmationLevel(): Int = prefs.getInt("confirmation_level", 1)
    private fun readMemoryEnabled(): Boolean = prefs.getBoolean("memory_enabled", true)
    private fun readMaxTaskDuration(): Int = prefs.getInt("max_task_duration_sec", 120)
    private fun readMaxRetries(): Int = prefs.getInt("max_retries", 3)
}

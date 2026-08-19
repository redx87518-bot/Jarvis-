package com.jarvis.launcher.features.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jarvis.launcher.features.settings.SettingsViewModel
import com.jarvis.launcher.ui.JarvisApp
import com.jarvis.launcher.ui.theme.JARVISTheme
import com.jarvis.core.JarvisThemeMode
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainActivityContent()
        }
    }
}

@Composable
fun MainActivityContent(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by settingsViewModel.uiState.collectAsStateWithLifecycle()

    JARVISTheme(
        themeMode = when (val mode = settings.themeMode) {
            is JarvisThemeMode.Dark -> JarvisThemeMode.Dark
            is JarvisThemeMode.Light -> JarvisThemeMode.Light
            is JarvisThemeMode.System -> JarvisThemeMode.System
        },
        dynamicColor = settings.dynamicColor,
    ) {
        JarvisApp()
    }
}

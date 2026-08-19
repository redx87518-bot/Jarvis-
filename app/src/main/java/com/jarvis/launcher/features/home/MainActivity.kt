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
import com.jarvis.launcher.ui.theme.JarvisThemeMode
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
        themeMode = when (settings.themeMode) {
            JarvisThemeMode.Dark -> JarvisThemeMode.Dark
            JarvisThemeMode.Light -> JarvisThemeMode.Light
            JarvisThemeMode.System -> JarvisThemeMode.System
        },
        dynamicColor = settings.dynamicColor,
    ) {
        JarvisApp()
    }
}

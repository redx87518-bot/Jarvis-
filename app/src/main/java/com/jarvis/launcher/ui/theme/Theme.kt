package com.jarvis.launcher.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    tertiary = DarkTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = DarkError,
    onError = DarkOnError,
)

private val LightColors = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    tertiary = LightTertiary,
    background = LightBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = LightError,
    onError = LightOnError,
)

private val DarkPrimary = Color(0xFF4A90D9)
private val DarkOnPrimary = Color(0xFFFFFFFF)
private val DarkPrimaryContainer = Color(0xFF1E3A6B)
private val DarkSecondary = Color(0xFF8E7CFA)
private val DarkOnSecondary = Color(0xFF000000)
private val DarkTertiary = Color(0xFF63D3D6)
private val DarkBackground = Color(0xFF0A0A0E)
private val DarkSurface = Color(0xFF1A1A22)
private val DarkOnSurface = Color(0xFFE5E5E5)
private val DarkOnSurfaceVariant = Color(0xFFA0A0AC)
private val DarkOutline = Color(0xFF404049)
private val DarkError = Color(0xFFFF5252)
private val DarkOnError = Color(0xFFFFFFFF)

private val LightPrimary = Color(0xFF2962CC)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightPrimaryContainer = Color(0xFFD6E4FF)
private val LightSecondary = Color(0xFF654B9E)
private val LightOnSecondary = Color(0xFFFFFFFF)
private val LightTertiary = Color(0xFF00828C)
private val LightBackground = Color(0xFFF8F8FC)
private val LightSurface = Color(0xFFFFFFFF)
private val LightOnSurface = Color(0xFF1A1A1A)
private val LightOnSurfaceVariant = Color(0xFF605A69)
private val LightOutline = Color(0xFFCBC9D2)
private val LightError = Color(0xFFFF5252)
private val LightOnError = Color(0xFFFFFFFF)

sealed class JarvisThemeMode {
    object System : JarvisThemeMode()
    object Light : JarvisThemeMode()
    object Dark : JarvisThemeMode()
}

data class JarvisColors(
    val background: Color = DarkBackground,
    val surface: Color = DarkSurface,
    val surfaceVariant: Color = DarkOnSurfaceVariant,
    val primary: Color = DarkPrimary,
    val secondary: Color = DarkSecondary,
    val textPrimary: Color = DarkOnSurface,
    val textSecondary: Color = DarkOnSurfaceVariant,
    val success: Color = Color(0xFF4CAF50),
    val warning: Color = Color(0xFFFF9800),
    val error: Color = DarkError,
    val orbIdle: Color = Color(0xFF8E7CFA),
    val orbRing: Color = Color(0xFF4A90D9),
)

val LocalJarvisColors = compositionLocalOf { JarvisColors() }
val LocalJarvisThemeMode = compositionLocalOf<JarvisThemeMode> { JarvisThemeMode.System }

@Composable
fun jarvisColorsFor(isDark: Boolean): JarvisColors {
    return if (isDark) {
        JarvisColors(
            background = DarkBackground,
            surface = DarkSurface,
            surfaceVariant = DarkOnSurfaceVariant,
            primary = DarkPrimary,
            secondary = DarkSecondary,
            textPrimary = DarkOnSurface,
            textSecondary = DarkOnSurfaceVariant,
            success = Color(0xFF4CAF50),
            warning = Color(0xFFFF9800),
            error = DarkError,
            orbIdle = Color(0xFF8E7CFA),
            orbRing = Color(0xFF4A90D9),
        )
    } else {
        JarvisColors(
            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightOnSurfaceVariant,
            primary = LightPrimary,
            secondary = LightSecondary,
            textPrimary = LightOnSurface,
            textSecondary = LightOnSurfaceVariant,
            success = Color(0xFF4CAF50),
            warning = Color(0xFFFF9800),
            error = LightError,
            orbIdle = Color(0xFF654B9E),
            orbRing = Color(0xFF2962CC),
        )
    }
}

@Composable
fun JARVISTheme(
    themeMode: JarvisThemeMode = LocalJarvisThemeMode.current,
    dynamicColor: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        is JarvisThemeMode.System -> isSystemDark
        is JarvisThemeMode.Light -> false
        is JarvisThemeMode.Dark -> true
    }

    val colorScheme = when {
        dynamicColor && isDark -> dynamicDarkColorScheme()
        dynamicColor && !isDark -> dynamicLightColorScheme()
        isDark -> DarkColors
        else -> LightColors
    }

    val jarvisColors = jarvisColorsFor(isDark)

    CompositionLocalProvider(
        LocalJarvisColors provides jarvisColors,
        LocalJarvisThemeMode provides themeMode,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}

@Composable
fun jarvisColors(): JarvisColors = LocalJarvisColors.current

@Composable
fun jarvisThemeMode(): JarvisThemeMode = LocalJarvisThemeMode.current

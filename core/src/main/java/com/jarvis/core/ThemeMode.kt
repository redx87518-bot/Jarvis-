package com.jarvis.core

sealed class JarvisThemeMode {
    object System : JarvisThemeMode()
    object Light : JarvisThemeMode()
    object Dark : JarvisThemeMode()
}

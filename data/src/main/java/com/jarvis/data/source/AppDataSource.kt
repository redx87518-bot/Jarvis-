package com.jarvis.data.source

import com.jarvis.core.AppCategory
import com.jarvis.core.AppInfo

interface AppDataSource {
    suspend fun getInstalledApps(): List<AppInfo>
    suspend fun getAppInfo(packageName: String): AppInfo?
    suspend fun getAppCategories(apps: List<AppInfo>): Map<AppCategory, List<AppInfo>>
}

data class AppPermissionInfo(
    val name: String,
    val isGranted: Boolean,
    val description: String,
)

data class AppExplanation(
    val appName: String,
    val packageName: String,
    val category: String,
    val description: String,
    val mainFunctions: List<String>,
    val usesInternet: Boolean,
    val permissions: List<AppPermissionInfo>,
    val privacySummary: String,
    val similarAppPackages: List<String>,
)

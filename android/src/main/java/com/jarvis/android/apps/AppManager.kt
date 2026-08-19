package com.jarvis.android.apps

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import com.jarvis.core.AppCategory
import com.jarvis.core.AppInfo
import com.jarvis.core.JarvisResult
import com.jarvis.core.memory.MemoryScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AppManager {
    suspend fun getInstalledApps(): List<AppInfo>
    suspend fun launchApp(packageName: String): JarvisResult<Unit>
    suspend fun getAppIcon(packageName: String): Drawable?
    suspend fun getAppLabel(packageName: String): String
    suspend fun isAppInstalled(packageName: String): Boolean
    suspend fun openAppInfo(packageName: String): JarvisResult<Unit>
    suspend fun uninstallApp(packageName: String): JarvisResult<Unit>
    fun observeApps(): Flow<List<AppInfo>>
    suspend fun getCategories(apps: List<AppInfo>): Map<AppCategory, List<AppInfo>>
}

class SystemAppManager(
    private val context: Context,
) : AppManager {

    private val packageManager = context.packageManager
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    private var appsLoaded = false

    override suspend fun getInstalledApps(): List<AppInfo> {
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.packageName != context.packageName }
            .map { info ->
                AppInfo(
                    packageName = info.packageName,
                    appName = packageManager.getApplicationLabel(info).toString(),
                    category = categorize(info.packageName),
                    isSystemApp = (info.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0,
                    isEnabled = info.enabled,
                    installTime = 0L,
                    usesInternet = false,
                )
            }
            .filter { it.appName.isNotBlank() }
            .sortedBy { it.appName }

        _apps.value = apps
        appsLoaded = true
        return apps
    }

    private fun categorize(pkg: String): AppCategory {
        val p = pkg.lowercase()
        return when {
            p.contains("whatsapp") || p.contains("messenger") || p.contains("telegram") -> AppCategory.COMMUNICATION
            p.contains("gmail") || p.contains("email") -> AppCategory.PRODUCTIVITY
            p.contains("chrome") || p.contains("browser") -> AppCategory.UTILITIES
            p.contains("github") -> AppCategory.DEVELOPMENT
            p.contains("youtube") || p.contains("camera") -> AppCategory.MEDIA
            p.contains("game") -> AppCategory.GAMING
            p.contains("bank") || p.contains("finance") -> AppCategory.FINANCE
            p.contains("duolingo") || p.contains("learn") -> AppCategory.EDUCATION
            else -> AppCategory.OTHER
        }
    }

    override suspend fun launchApp(packageName: String): JarvisResult<Unit> {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                updateLastUsed(packageName)
                JarvisResult.Success(Unit)
            } else {
                JarvisResult.Error("App not found: $packageName")
            }
        } catch (e: ActivityNotFoundException) {
            JarvisResult.Error("Cannot launch app: ${e.message}", e)
        } catch (e: SecurityException) {
            JarvisResult.Error("Permission denied: ${e.message}", e)
        }
    }

    override suspend fun getAppIcon(packageName: String): Drawable? {
        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    override suspend fun getAppLabel(packageName: String): String {
        return try {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(packageName, 0)
            ).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    override suspend fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override suspend fun openAppInfo(packageName: String): JarvisResult<Unit> {
        return try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            JarvisResult.Success(Unit)
        } catch (e: Exception) {
            JarvisResult.Error("Cannot open app info: ${e.message}", e)
        }
    }

    override suspend fun uninstallApp(packageName: String): JarvisResult<Unit> {
        return try {
            val intent = Intent(Intent.ACTION_DELETE, Uri.fromParts("package", packageName, null))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            JarvisResult.Success(Unit)
        } catch (e: Exception) {
            JarvisResult.Error("Cannot uninstall: ${e.message}", e)
        }
    }

    override fun observeApps(): Flow<List<AppInfo>> = _apps.asStateFlow()

    override suspend fun getCategories(apps: List<AppInfo>): Map<AppCategory, List<AppInfo>> =
        apps.groupBy { it.category }

    private fun updateLastUsed(packageName: String) {
        val current = _apps.value.toMutableList()
        current.replaceAll { app ->
            if (app.packageName == packageName) {
                app.copy(lastUsedTime = System.currentTimeMillis())
            } else app
        }
        _apps.value = current
    }
}

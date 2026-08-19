package com.jarvis.data.source

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.jarvis.core.AppCategory
import com.jarvis.core.AppInfo
import javax.inject.Inject

class SystemAppDataSource @Inject constructor(
    private val packageManager: PackageManager,
    private val ownPackageName: String,
) : AppDataSource {

    override suspend fun getInstalledApps(): List<AppInfo> {
        val installedApps = buildList {
            val installed = packageManager.getInstalledApplications(
                PackageManager.GET_META_DATA or PackageManager.GET_ACTIVITIES or
                    PackageManager.GET_SERVICES or PackageManager.GET_RECEIVERS
            )
            for (info in installed) {
                if (info.packageName == ownPackageName) continue
                val appName = packageManager.getApplicationLabel(info).toString()
                if (appName.isBlank()) continue
                val item = try {
                    AppInfo(
                        packageName = info.packageName,
                        appName = appName,
                        category = categorizeApp(info.packageName),
                        isSystemApp = (info.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                        isEnabled = info.enabled,
                        installTime = getFirstInstallTime(info.packageName),
                        usesInternet = hasInternetPermission(info.packageName),
                    )
                } catch (e: Exception) {
                    continue
                }
                add(item)
            }
        }
        return installedApps.sortedBy { it.appName }
    }

    override suspend fun getAppInfo(packageName: String): AppInfo? {
        return try {
            val info = packageManager.getApplicationInfo(packageName, 0)
            AppInfo(
                packageName = info.packageName,
                appName = packageManager.getApplicationLabel(info).toString(),
                category = categorizeApp(info.packageName),
                isSystemApp = (info.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                isEnabled = info.enabled,
                installTime = getFirstInstallTime(info.packageName),
                usesInternet = hasInternetPermission(info.packageName),
            )
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    override suspend fun getAppCategories(apps: List<AppInfo>): Map<AppCategory, List<AppInfo>> {
        return apps.groupBy { it.category }
    }

    private fun categorizeApp(packageName: String): AppCategory {
        val pkg = packageName.lowercase()
        return when {
            pkg.contains("whatsapp") || pkg.contains("messenger") || pkg.contains("telegram") ||
            pkg.contains("signal") || pkg.contains("discord") || pkg.contains("facebook") -> AppCategory.COMMUNICATION
            pkg.contains("gmail") || pkg.contains("email") || pkg.contains("outlook") ||
            pkg.contains("drive") || pkg.contains("docs") || pkg.contains("sheets") -> AppCategory.PRODUCTIVITY
            pkg.contains("chrome") || pkg.contains("browser") || pkg.contains("edge") -> AppCategory.UTILITIES
            pkg.contains("github") || pkg.contains("gitlab") || pkg.contains("code") -> AppCategory.DEVELOPMENT
            pkg.contains("youtube") || pkg.contains("netflix") || pkg.contains("music") ||
            pkg.contains("camera") || pkg.contains("gallery") -> AppCategory.MEDIA
            pkg.contains("maps") || pkg.contains("uber") || pkg.contains("travel") -> AppCategory.TRAVEL
            pkg.contains("bank") || pkg.contains("finance") || pkg.contains("paypal") ||
            pkg.contains("wallet") -> AppCategory.FINANCE
            pkg.contains("duolingo") || pkg.contains("udemy") || pkg.contains("coursera") ||
            pkg.contains("study") -> AppCategory.EDUCATION
            pkg.contains("game") || pkg.contains("play") && !pkg.contains("googleplay") -> AppCategory.GAMING
            pkg.contains("fitness") || pkg.contains("health") || pkg.contains("medisafe") -> AppCategory.HEALTH
            pkg.contains("secur") || pkg.contains("cyber") || pkg.contains("vpn") ||
            pkg.contains("antivirus") -> AppCategory.CYBERSECURITY
            else -> AppCategory.OTHER
        }
    }

    private fun getFirstInstallTime(packageName: String): Long {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(0)
                ).firstInstallTime
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0).firstInstallTime
            }
        } catch (e: Exception) {
            0L
        }
    }

    private fun hasInternetPermission(packageName: String): Boolean {
        return try {
            val pkgInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            }
            val perms = pkgInfo.requestedPermissions ?: emptyList()
            perms.any { it == android.Manifest.permission.INTERNET }
        } catch (e: Exception) {
            false
        }
    }
}

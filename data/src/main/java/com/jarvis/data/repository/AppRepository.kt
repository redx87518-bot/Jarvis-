package com.jarvis.data.repository

import com.jarvis.core.AppCategory
import com.jarvis.core.AppInfo
import com.jarvis.data.dao.AppInfoDao
import com.jarvis.data.entities.AppInfoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val appInfoDao: AppInfoDao,
) {
    fun getAllEnabled(): Flow<List<AppInfo>> = appInfoDao.getAllEnabled()
        .map { list -> list.map { it.toDomain() } }

    fun getFavorites(): Flow<List<AppInfo>> = appInfoDao.getFavorites()
        .map { list -> list.map { it.toDomain() } }

    fun getRecent(): Flow<List<AppInfo>> = appInfoDao.getRecent()
        .map { list -> list.map { it.toDomain() } }

    suspend fun getByPackageName(packageName: String): AppInfo? =
        appInfoDao.getByPackageName(packageName)?.toDomain()

    suspend fun search(query: String): List<AppInfo> =
        appInfoDao.search(query).map { it.toDomain() }

    suspend fun insert(app: AppInfo, isFavorite: Boolean = false, aiExplanation: String? = null) {
        appInfoDao.insert(
            app.toEntity().copy(isFavorite = isFavorite, aiExplanation = aiExplanation)
        )
    }

    suspend fun insertAll(apps: List<AppInfo>) {
        appInfoDao.insertAll(apps.map { it.toEntity() })
    }

    suspend fun setFavorite(packageName: String, isFavorite: Boolean) =
        appInfoDao.setFavorite(packageName, isFavorite)

    suspend fun updateLastUsed(packageName: String) =
        appInfoDao.updateLastUsed(packageName, System.currentTimeMillis())

    fun naturalLanguageSearch(query: String, allApps: List<AppInfo>): List<AppInfo> {
        val lower = query.lowercase().trim()
        val exact = allApps.filter { it.appName.equals(lower, ignoreCase = true) }
        if (exact.isNotEmpty()) return exact
        val nameMatch = allApps.filter {
            it.appName.contains(lower, ignoreCase = true) ||
            it.packageName.contains(lower, ignoreCase = true)
        }
        if (nameMatch.isNotEmpty()) return nameMatch
        val categoryMatch = allApps.filter {
            it.category.isRelevantFor(lower)
        }
        return if (categoryMatch.isNotEmpty()) categoryMatch
        else allApps.filter {
            it.description?.contains(lower, ignoreCase = true) == true
        }
    }
}

private fun AppInfoEntity.toDomain(): AppInfo = AppInfo(
    packageName = packageName,
    appName = appName,
    category = category,
    isSystemApp = isSystemApp,
    isEnabled = isEnabled,
    lastUsedTime = lastUsedTime,
    installTime = installTime,
    iconRes = null,
    description = description,
    usesInternet = usesInternet,
)

private fun AppInfo.toEntity(): AppInfoEntity = AppInfoEntity(
    packageName = packageName,
    appName = appName,
    category = category,
    isSystemApp = isSystemApp,
    isEnabled = isEnabled,
    lastUsedTime = lastUsedTime,
    installTime = installTime,
    description = description,
    usesInternet = usesInternet,
    isFavorite = false,
    aiExplanation = null,
)

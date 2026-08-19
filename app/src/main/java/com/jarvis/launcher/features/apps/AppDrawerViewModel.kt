package com.jarvis.launcher.features.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.core.AppCategory
import com.jarvis.core.AppInfo
import com.jarvis.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AppDrawerUiState(
    val allApps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: AppCategory? = null,
    val categories: List<CategoryCount> = emptyList(),
    val isLoading: Boolean = false,
    val currentTab: AppDrawerTab = AppDrawerTab.ALL,
)

data class CategoryCount(
    val category: AppCategory,
    val count: Int,
    val icon: String,
)

enum class AppDrawerTab {
    ALL, RECENT, FAVORITES, CATEGORIES, SUGGESTED
}

@HiltViewModel
class AppDrawerViewModel @Inject constructor(
    private val appRepository: AppRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val uiState = appRepository.getAllEnabled()
        .map { apps ->
            val favorites = apps.filter { it.lastUsedTime > System.currentTimeMillis() - 86400000 * 7 }
            AppDrawerUiState(
                allApps = apps,
                filteredApps = if (_searchQuery.value.isBlank()) apps else filterApps(apps, _searchQuery.value),
                categories = buildCategories(apps),
                isLoading = false,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppDrawerUiState(isLoading = true))

    fun search(query: String): List<AppInfo> {
        _searchQuery.value = query
        val currentApps = uiState.value.allApps
        return if (query.isBlank()) currentApps else filterApps(currentApps, query)
    }

    private fun filterApps(apps: List<AppInfo>, query: String): List<AppInfo> {
        val lower = query.lowercase().trim()
        if (lower.isBlank()) return apps

        val exactName = apps.filter { it.appName.equals(lower, ignoreCase = true) }
        if (exactName.isNotEmpty()) return exactName

        val nameMatch = apps.filter {
            it.appName.contains(lower, ignoreCase = true) ||
            it.packageName.contains(lower, ignoreCase = true)
        }
        if (nameMatch.isNotEmpty()) return nameMatch

        val categoryMatch = apps.filter { it.category.isRelevantFor(lower) }
        return categoryMatch.ifEmpty {
            apps.filter {
                it.description?.contains(lower, ignoreCase = true) == true
            }
        }
    }

    fun naturalLanguageSearch(query: String): List<AppInfo> {
        val apps = uiState.value.allApps
        return appRepository.naturalLanguageSearch(query, apps)
    }

    private fun buildCategories(apps: List<AppInfo>): List<CategoryCount> {
        val counts = apps.groupBy { it.category }
        return counts.map { (cat, list) ->
            CategoryCount(
                category = cat,
                count = list.size,
                icon = categoryIcon(cat),
            )
        }.sortedByDescending { it.count }
    }

    private fun categoryIcon(category: AppCategory): String = when (category) {
        AppCategory.COMMUNICATION -> "💬"
        AppCategory.PRODUCTIVITY -> "📋"
        AppCategory.SOCIAL -> "👥"
        AppCategory.MEDIA -> "🎵"
        AppCategory.UTILITIES -> "🔧"
        AppCategory.FINANCE -> "💰"
        AppCategory.EDUCATION -> "📚"
        AppCategory.TRAVEL -> "🗺️"
        AppCategory.HEALTH -> "🏥"
        AppCategory.GAMING -> "🎮"
        AppCategory.DEVELOPMENT -> "💻"
        AppCategory.CYBERSECURITY -> "🛡️"
        AppCategory.OTHER -> "📦"
    }
}

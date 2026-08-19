package com.jarvis.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jarvis.core.AppCategory

@Entity(tableName = "apps")
data class AppInfoEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val category: AppCategory,
    val isSystemApp: Boolean,
    val isEnabled: Boolean,
    val lastUsedTime: Long,
    val installTime: Long,
    val description: String?,
    val usesInternet: Boolean,
    val isFavorite: Boolean = false,
    val aiExplanation: String? = null,
    val lastInteractionTime: Long = 0L,
)

package com.jarvis.data

import androidx.room.TypeConverter
import com.jarvis.core.AppCategory
import com.jarvis.core.tasks.TaskStatus
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.toMap
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.String.serializer
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return json.encodeToString(ListSerializer(String.serializer()), value ?: emptyList())
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return json.decodeFromString(ListSerializer(String.serializer()), value)
    }

    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String {
        return json.encodeToString(MapSerializer(String.serializer(), String.serializer()), value ?: emptyMap())
    }

    @TypeConverter
    fun toStringMap(value: String?): Map<String, String> {
        if (value.isNullOrEmpty()) return emptyMap()
        return json.decodeFromString(MapSerializer(String.serializer(), String.serializer()), value)
    }

    @TypeConverter
    fun fromAppCategory(value: AppCategory): String = value.name

    @TypeConverter
    fun toAppCategory(value: String): AppCategory = runCatching { AppCategory.valueOf(value) }.getOrDefault(AppCategory.OTHER)

    @TypeConverter
    fun fromTaskStatus(value: TaskStatus): String = value.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = runCatching { TaskStatus.valueOf(value) }.getOrDefault(TaskStatus.CREATED)

    @TypeConverter
    fun fromStringListSet(value: Set<String>?): String {
        return json.encodeToString(SetSerializer(String.serializer()), value ?: emptySet())
    }

    @TypeConverter
    fun toStringSet(value: String?): Set<String> {
        if (value.isNullOrEmpty()) return emptySet()
        return json.decodeFromString(SetSerializer(String.serializer()), value)
    }
}

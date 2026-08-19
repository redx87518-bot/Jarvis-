package com.jarvis.core.memory

import kotlinx.serialization.Serializable

@Serializable
data class MemoryItem(
    val id: String,
    val key: String,
    val value: String,
    val category: MemoryCategory,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = createdAt,
    val scope: MemoryScope = MemoryScope.LONG_TERM,
    val ttlSeconds: Long? = null,
) {
    val isExpired: Boolean
        get() = ttlSeconds != null && (System.currentTimeMillis() - createdAt) > ttlSeconds * 1000
}

@Serializable
enum class MemoryCategory {
    PREFERENCE,
    FACT,
    CONVERSATION,
    TASK_CONTEXT,
    CREDENTIAL_REF,
}

@Serializable
enum class MemoryScope {
    SHORT_TERM,
    LONG_TERM,
    TASK,
}

interface MemoryStore {
    suspend fun save(item: MemoryItem)
    suspend fun get(key: String): MemoryItem?
    suspend fun query(prefix: String): List<MemoryItem>
    suspend fun delete(id: String)
    suspend fun clearAll()
    suspend fun observe(): kotlinx.coroutines.flow.Flow<List<MemoryItem>>
}

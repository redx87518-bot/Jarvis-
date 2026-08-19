package com.jarvis.data.repository

import com.jarvis.core.memory.MemoryCategory
import com.jarvis.core.memory.MemoryItem
import com.jarvis.core.memory.MemoryScope
import com.jarvis.core.memory.MemoryStore
import com.jarvis.core.memory.InMemoryMemoryStore
import com.jarvis.data.dao.MemoryDao
import com.jarvis.data.entities.MemoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class MemoryRepository @Inject constructor(
    private val memoryDao: MemoryDao,
) : MemoryStore {

    override suspend fun save(item: MemoryItem) {
        memoryDao.insert(item.toEntity())
    }

    override suspend fun get(key: String): MemoryItem? =
        memoryDao.getByKey(key)?.toDomain()

    override suspend fun query(prefix: String): List<MemoryItem> =
        memoryDao.getByPrefix(prefix).map { it.toDomain() }

    override suspend fun delete(id: String) {
        memoryDao.deleteById(id)
    }

    override suspend fun clearAll() {
        memoryDao.clearAll()
    }

    override suspend fun observe(): Flow<List<MemoryItem>> =
        memoryDao.getAll().map { list -> list.map { it.toDomain() } }
}

private fun MemoryEntity.toDomain(): MemoryItem = MemoryItem(
    id = id,
    key = key,
    value = value,
    category = category,
    createdAt = createdAt,
    updatedAt = updatedAt,
    scope = scope,
    ttlSeconds = ttlSeconds,
)

private fun MemoryItem.toEntity(): MemoryEntity = MemoryEntity(
    id = id.ifEmpty { UUID.randomUUID().toString() },
    key = key,
    value = value,
    category = category,
    scope = scope,
    createdAt = createdAt,
    updatedAt = updatedAt,
    ttlSeconds = ttlSeconds,
)

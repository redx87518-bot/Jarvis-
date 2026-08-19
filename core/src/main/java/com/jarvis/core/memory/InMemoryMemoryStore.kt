package com.jarvis.core.memory

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

class InMemoryMemoryStore : MemoryStore {

    private val store = ConcurrentHashMap<String, MemoryItem>()
    private val flow = MutableStateFlow<List<MemoryItem>>(emptyList())

    private fun refresh() {
        flow.value = store.values.filter { !it.isExpired }.sortedByDescending { it.updatedAt }
    }

    override suspend fun save(item: MemoryItem) {
        store[item.id] = item
        refresh()
    }

    override suspend fun get(key: String): MemoryItem? {
        return store[key]?.takeIf { !it.isExpired }
    }

    override suspend fun query(prefix: String): List<MemoryItem> {
        return store.values.filter { !it.isExpired }
            .filter { it.key.startsWith(prefix, ignoreCase = true) || it.value.contains(prefix, ignoreCase = true) }
            .sortedByDescending { it.updatedAt }
    }

    override suspend fun delete(id: String) {
        store.remove(id)
        refresh()
    }

    override suspend fun clearAll() {
        store.clear()
        refresh()
    }

    override suspend fun observe(): Flow<List<MemoryItem>> = flow
}

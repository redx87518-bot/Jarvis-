package com.jarvis.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jarvis.core.memory.MemoryCategory
import com.jarvis.core.memory.MemoryScope

@Entity(tableName = "memory")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val key: String,
    val value: String,
    val category: MemoryCategory,
    val scope: MemoryScope,
    val createdAt: Long,
    val updatedAt: Long,
    val ttlSeconds: Long?,
)

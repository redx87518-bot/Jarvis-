package com.jarvis.data.dao

import androidx.room.*
import com.jarvis.data.entities.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memory ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memory WHERE key = :key LIMIT 1")
    suspend fun getByKey(key: String): MemoryEntity?

    @Query("SELECT * FROM memory WHERE key LIKE :prefix || '%' ORDER BY updatedAt DESC")
    suspend fun getByPrefix(prefix: String): List<MemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memory: MemoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(memories: List<MemoryEntity>)

    @Query("DELETE FROM memory WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM memory")
    suspend fun clearAll()
}

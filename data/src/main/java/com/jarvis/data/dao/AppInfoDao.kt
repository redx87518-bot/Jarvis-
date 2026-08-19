package com.jarvis.data.dao

import androidx.room.*
import com.jarvis.data.entities.AppInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppInfoDao {
    @Query("SELECT * FROM apps WHERE isEnabled = 1 ORDER BY appName ASC")
    fun getAllEnabled(): Flow<List<AppInfoEntity>>

    @Query("SELECT * FROM apps WHERE isFavorite = 1 ORDER BY appName ASC")
    fun getFavorites(): Flow<List<AppInfoEntity>>

    @Query("SELECT * FROM apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getByPackageName(packageName: String): AppInfoEntity?

    @Query("SELECT * FROM apps WHERE appName LIKE :query || '%' ORDER BY lastUsedTime DESC LIMIT 10")
    suspend fun search(query: String): List<AppInfoEntity>

    @Query("SELECT * FROM apps WHERE lastUsedTime > 0 ORDER BY lastUsedTime DESC LIMIT 20")
    fun getRecent(): Flow<List<AppInfoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: AppInfoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<AppInfoEntity>)

    @Update
    suspend fun update(app: AppInfoEntity)

    @Query("UPDATE apps SET isFavorite = :isFavorite WHERE packageName = :packageName")
    suspend fun setFavorite(packageName: String, isFavorite: Boolean)

    @Query("UPDATE apps SET lastUsedTime = :time WHERE packageName = :packageName")
    suspend fun updateLastUsed(packageName: String, time: Long)

    @Delete
    suspend fun delete(app: AppInfoEntity)

    @Query("DELETE FROM apps")
    suspend fun clearAll()
}

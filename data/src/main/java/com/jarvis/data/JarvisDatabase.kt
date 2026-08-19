package com.jarvis.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jarvis.data.entities.AppInfoEntity
import com.jarvis.data.entities.MemoryEntity
import com.jarvis.data.entities.TaskStepEntity
import com.jarvis.data.entities.TaskEntity
import com.jarvis.data.dao.AppInfoDao
import com.jarvis.data.dao.MemoryDao
import com.jarvis.data.dao.TaskDao
import com.jarvis.data.dao.TaskStepDao

@Database(
    entities = [
        AppInfoEntity::class,
        TaskEntity::class,
        TaskStepEntity::class,
        MemoryEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class JarvisDatabase : RoomDatabase() {
    abstract fun appInfoDao(): AppInfoDao
    abstract fun taskDao(): TaskDao
    abstract fun taskStepDao(): TaskStepDao
    abstract fun memoryDao(): MemoryDao

    companion object {
        const val DB_NAME = "jarvis.db"
    }
}

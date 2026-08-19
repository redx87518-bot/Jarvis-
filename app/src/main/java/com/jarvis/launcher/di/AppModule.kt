package com.jarvis.launcher.di

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.room.Room
import com.jarvis.android.apps.AppManager
import com.jarvis.android.apps.SystemAppManager
import com.jarvis.core.ai.ModelRouter
import com.jarvis.core.ai.StubModelRouter
import com.jarvis.core.agent.AgentOrchestrator
import com.jarvis.core.agent.JarvisAgentOrchestrator
import com.jarvis.core.agent.ToolRegistry
import com.jarvis.core.intent.IntentManager
import com.jarvis.core.intent.LocalIntentManager
import com.jarvis.core.memory.InMemoryMemoryStore
import com.jarvis.core.memory.MemoryStore
import com.jarvis.core.tasks.InMemoryTaskManager
import com.jarvis.core.tasks.TaskManager
import com.jarvis.data.JarvisDatabase
import com.jarvis.data.dao.AppInfoDao
import com.jarvis.data.dao.MemoryDao
import com.jarvis.data.dao.TaskDao
import com.jarvis.data.dao.TaskStepDao
import com.jarvis.data.repository.AppRepository
import com.jarvis.data.repository.MemoryRepository
import com.jarvis.data.repository.TaskRepository
import com.jarvis.data.source.AppDataSource
import com.jarvis.data.source.SystemAppDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): JarvisDatabase =
        Room.databaseBuilder(
            context,
            JarvisDatabase::class.java,
            JarvisDatabase.DB_NAME,
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideAppInfoDao(db: JarvisDatabase): AppInfoDao = db.appInfoDao()

    @Provides
    fun provideTaskDao(db: JarvisDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideTaskStepDao(db: JarvisDatabase): TaskStepDao = db.taskStepDao()

    @Provides
    fun provideMemoryDao(db: JarvisDatabase): MemoryDao = db.memoryDao()

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager =
        context.packageManager

    @Provides
    @Singleton
    fun provideOwnPackageName(@ApplicationContext context: Context): String =
        context.packageName

    @Provides
    @Singleton
    fun provideAppDataSource(
        packageManager: PackageManager,
        ownPackageName: String,
    ): AppDataSource = SystemAppDataSource(packageManager, ownPackageName)

    @Provides
    @Singleton
    fun provideAppManager(@ApplicationContext context: Context): AppManager =
        SystemAppManager(context)

    @Provides
    @Singleton
    fun provideAppRepository(appInfoDao: AppInfoDao): AppRepository = AppRepository(appInfoDao)

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository = TaskRepository(taskDao)

    @Provides
    @Singleton
    fun provideMemoryRepository(memoryDao: MemoryDao): MemoryRepository = MemoryRepository(memoryDao)

    @Provides
    @Singleton
    fun provideIntentManager(): IntentManager = LocalIntentManager()

    @Provides
    @Singleton
    fun provideMemoryStore(): MemoryStore = InMemoryMemoryStore()

    @Provides
    @Singleton
    fun provideTaskManager(): TaskManager = InMemoryTaskManager()

    @Provides
    @Singleton
    fun provideModelRouter(): ModelRouter = StubModelRouter()

    @Provides
    @Singleton
    fun provideToolRegistry(): ToolRegistry = ToolRegistry(emptyList())

    @Provides
    @Singleton
    fun provideAgentOrchestrator(
        intentManager: IntentManager,
        taskManager: TaskManager,
        memoryStore: MemoryStore,
        modelRouter: ModelRouter,
        toolRegistry: ToolRegistry,
    ): AgentOrchestrator = JarvisAgentOrchestrator(
        intentManager = intentManager,
        taskManager = taskManager,
        memoryStore = memoryStore,
        modelRouter = modelRouter,
        toolRegistry = toolRegistry,
    )
}

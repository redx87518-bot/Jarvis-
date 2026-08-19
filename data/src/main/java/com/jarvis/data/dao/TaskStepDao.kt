package com.jarvis.data.dao

import androidx.room.*
import com.jarvis.data.entities.TaskStepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskStepDao {
    @Query("SELECT * FROM task_steps WHERE taskId = :taskId ORDER BY stepOrder ASC")
    fun getByTask(taskId: String): Flow<List<TaskStepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(step: TaskStepEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(steps: List<TaskStepEntity>)

    @Update
    suspend fun update(step: TaskStepEntity)

    @Query("UPDATE task_steps SET status = :status, result = :result WHERE id = :id")
    suspend fun updateStatus(id: String, status: com.jarvis.core.tasks.TaskStatus, result: String)

    @Query("DELETE FROM task_steps WHERE taskId = :taskId")
    suspend fun deleteByTask(taskId: String)
}

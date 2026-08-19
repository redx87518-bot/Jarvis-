package com.jarvis.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jarvis.core.tasks.TaskStatus

@Entity(
    tableName = "task_steps",
    indices = [Index(value = ["taskId"])],
)
data class TaskStepEntity(
    @PrimaryKey val id: String,
    val taskId: String,
    val name: String,
    val description: String,
    val status: TaskStatus,
    val result: String,
    val stepOrder: Int,
)

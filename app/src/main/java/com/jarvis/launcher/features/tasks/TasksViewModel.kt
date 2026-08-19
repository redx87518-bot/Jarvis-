package com.jarvis.launcher.features.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.core.tasks.Task
import com.jarvis.core.tasks.TaskFilter
import com.jarvis.core.tasks.TaskManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.jarvis.core.tasks.isActive
import javax.inject.Inject

data class TasksUiState(
    val activeTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val failedTasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val selectedFilter: TaskFilter = TaskFilter.ACTIVE,
)

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskManager: TaskManager,
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TaskFilter.ACTIVE)
    val selectedFilter = _selectedFilter.asStateFlow()

    val activeTasks = flow {
        taskManager.observeTasks().collect { tasks ->
            emit(tasks.filter { it.isActive })
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTasks = flow {
        taskManager.observeTasks().collect { tasks ->
            emit(tasks.filter { it.status.name == "COMPLETED" })
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val failedTasks = flow {
        taskManager.observeTasks().collect { tasks ->
            emit(tasks.filter { it.status.name == "FAILED" })
        }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState = _selectedFilter
        .map { filter ->
            when (filter) {
                TaskFilter.ACTIVE -> TasksUiState(activeTasks = activeTasks.value, selectedFilter = filter)
                TaskFilter.COMPLETED -> TasksUiState(completedTasks = completedTasks.value, selectedFilter = filter)
                TaskFilter.FAILED -> TasksUiState(failedTasks = failedTasks.value, selectedFilter = filter)
                else -> TasksUiState(selectedFilter = filter)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TasksUiState())

    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    fun cancelTask(taskId: String) {
        viewModelScope.launch {
            taskManager.cancelTask(taskId)
        }
    }

    fun pauseTask(taskId: String) {
        viewModelScope.launch {
            taskManager.pauseTask(taskId)
        }
    }

    fun resumeTask(taskId: String) {
        viewModelScope.launch {
            taskManager.resumeTask(taskId)
        }
    }

    suspend fun getTask(id: String): Task? = taskManager.getTask(id)
}

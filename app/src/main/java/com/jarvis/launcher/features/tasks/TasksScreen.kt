package com.jarvis.launcher.features.tasks

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jarvis.core.tasks.Task
import com.jarvis.core.tasks.TaskFilter
import com.jarvis.core.tasks.TaskStatus
import com.jarvis.core.tasks.isActive
import com.jarvis.launcher.ui.components.TaskTimeline
import com.jarvis.launcher.ui.components.TaskStepItem
import com.jarvis.launcher.ui.components.TaskStepStatus
import com.jarvis.launcher.ui.theme.jarvisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
) {
    val colors = jarvisColors()
    val viewModel: TasksViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val activeTasks by viewModel.activeTasks.collectAsStateWithLifecycle()
    val completedTasks by viewModel.completedTasks.collectAsStateWithLifecycle()
    val failedTasks by viewModel.failedTasks.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.textPrimary,
                    )
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background,
                    titleContentColor = colors.textPrimary,
                ),
                actions = {
                    Row {
                        TextButton(onClick = {}) {
                            Text(text = "Active", color = colors.primary)
                        }
                        TextButton(onClick = {}) {
                            Text(text = "Completed", color = colors.textSecondary)
                        }
                        TextButton(onClick = {}) {
                            Text(text = "Failed", color = colors.textSecondary)
                        }
                    }
                },
            )
        },
        containerColor = colors.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = colors.primary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New task",
                    tint = colors.textPrimary,
                )
            }
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(colors.background),
        ) {
            if (activeTasks.isNotEmpty()) {
                TaskSection(
                    title = "Running",
                    tasks = activeTasks,
                    onTaskClick = { /* navigate to detail */ },
                    onCancel = { viewModel.cancelTask(it) },
                    onPause = { viewModel.pauseTask(it) },
                    onResume = { viewModel.resumeTask(it) },
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No active tasks.",
                        color = colors.textSecondary,
                    )
                }
            }

            if (completedTasks.isNotEmpty()) {
                TaskSection(
                    title = "Completed",
                    tasks = completedTasks,
                    onTaskClick = { },
                    onCancel = { },
                    onPause = { },
                    onResume = { },
                    showActions = false,
                )
            }

            if (failedTasks.isNotEmpty()) {
                TaskSection(
                    title = "Failed",
                    tasks = failedTasks,
                    onTaskClick = { },
                    onCancel = { viewModel.cancelTask(it) },
                    onPause = { },
                    onResume = { viewModel.resumeTask(it) },
                )
            }
        }
    }
}

@Composable
fun TaskSection(
    title: String,
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    onCancel: (String) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    showActions: Boolean = true,
) {
    val colors = jarvisColors()
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        tasks.forEach { task ->
            TaskCard(
                task = task,
                onClick = { onTaskClick(task.id) },
                showActions = showActions,
                onCancel = { onCancel(task.id) },
                onPause = { onPause(task.id) },
                onResume = { onResume(task.id) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onCancel: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    showActions: Boolean = true,
) {
    val colors = jarvisColors()
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = statusLabel(task.status),
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor(task.status, colors),
                    fontSize = 11.sp,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = task.goal,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (task.failedAction.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Failed: ${task.failedAction}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.error,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (showActions && task.isActive) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = { onPause() }) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Pause", color = colors.textSecondary, fontSize = 12.sp)
                    }
                    TextButton(onClick = { onResume() }) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Resume",
                            tint = colors.primary,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Resume", color = colors.primary, fontSize = 12.sp)
                    }
                    TextButton(onClick = { onCancel() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = colors.error,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Cancel", color = colors.error, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

private fun statusLabel(status: TaskStatus): String = when (status) {
    TaskStatus.RUNNING -> "Running"
    TaskStatus.CREATED -> "Pending"
    TaskStatus.PLANNING -> "Planning"
    TaskStatus.WAITING_FOR_USER -> "Waiting"
    TaskStatus.VERIFYING -> "Verifying"
    TaskStatus.RECOVERING -> "Recovering"
    TaskStatus.COMPLETED -> "Completed"
    TaskStatus.FAILED -> "Failed"
    TaskStatus.CANCELLED -> "Cancelled"
}

@Composable
private fun statusColor(status: TaskStatus, colors: com.jarvis.launcher.ui.theme.JarvisColors): Color = when (status) {
    TaskStatus.COMPLETED -> colors.success
    TaskStatus.FAILED -> colors.error
    TaskStatus.RUNNING -> colors.primary
    TaskStatus.WAITING_FOR_USER -> colors.warning
    else -> colors.textSecondary
}

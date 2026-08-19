package com.jarvis.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jarvis.launcher.ui.theme.jarvisColors

data class TaskStepItem(
    val name: String,
    val status: TaskStepStatus,
    val description: String = "",
)

enum class TaskStepStatus {
    PENDING, IN_PROGRESS, COMPLETED, FAILED;
}

@Composable
fun TaskTimeline(
    title: String,
    steps: List<TaskStepItem>,
    modifier: Modifier = Modifier,
    currentStep: Int = -1,
) {
    val colors = jarvisColors()
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        color = colors.surface,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(12.dp))

            steps.forEachIndexed { index, step ->
                TaskStepRow(
                    step = step,
                    isCurrent = index == currentStep,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun TaskStepRow(
    step: TaskStepItem,
    isCurrent: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = jarvisColors()
    val icon = when (step.status) {
        TaskStepStatus.COMPLETED -> Icons.Default.Check
        TaskStepStatus.FAILED -> Icons.Default.Error
        TaskStepStatus.IN_PROGRESS -> Icons.Default.Refresh
        TaskStepStatus.PENDING -> Icons.Default.RadioButtonUnchecked
    }
    val iconTint = when (step.status) {
        TaskStepStatus.COMPLETED -> colors.success
        TaskStepStatus.FAILED -> colors.error
        TaskStepStatus.IN_PROGRESS -> colors.primary
        TaskStepStatus.PENDING -> colors.textSecondary
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = step.status.name,
            tint = iconTint,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = step.name,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isCurrent) colors.primary else colors.textPrimary,
            fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )
    }
}

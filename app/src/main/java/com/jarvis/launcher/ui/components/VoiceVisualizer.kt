package com.jarvis.launcher.ui.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jarvis.launcher.ui.theme.jarvisColors

@Composable
fun VoiceVisualizer(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier,
    barCount: Int = 32,
    animate: Boolean = true,
) {
    val colors = jarvisColors()
    val barAnim = rememberInfiniteTransition(label = "voice-bars")
    val phase by barAnim.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = CubicBezierEasing(0f, 0f, 1f, 1f)),
            repeatMode = RepeatMode.Restart,
        ),
        label = "voice-bars-phase",
    )

    Canvas(modifier = modifier.fillMaxHeight()) {
        val barWidth = size.width / barCount
        for (i in 0 until barCount) {
            val height = amplitudes.getOrElse(i) { 0f }
            val barHeight = (height * size.height).coerceAtLeast(2f)
            val x = i * barWidth
            drawRoundRect(
                color = if (animate) colors.primary.copy(alpha = 0.5f + 0.5f * height) else colors.primary,
                topLeft = Offset(x, size.height - barHeight),
                size = Size(barWidth * 0.7f, barHeight),
                cornerRadius = 4f,
            )
        }
    }
}

@Composable
fun JarvisVoiceButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = jarvisColors()
    val orbColor = colors.primary

    IconButton(
        onClick = onClick,
        modifier = modifier.size(72.dp),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(colors.surface)
                .border(
                    width = 2.dp,
                    color = orbColor,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Talk to JARVIS",
                tint = orbColor,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}

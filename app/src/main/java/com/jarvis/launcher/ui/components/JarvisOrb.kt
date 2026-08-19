package com.jarvis.launcher.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jarvis.launcher.ui.theme.jarvisColors

enum class OrbState {
    IDLE,
    LISTENING,
    PROCESSING,
    THINKING,
    SPEAKING,
    WAITING_FOR_CONFIRMATION,
    SUCCESS,
    ERROR;
}

@Composable
fun JarvisOrb(
    state: OrbState = OrbState.IDLE,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier,
) {
    val colors = jarvisColors()
    val pulseProgress by rememberInfiniteTransition(label = "orb-pulse").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orb-pulse",
    )

    val ringScale by animateFloatAsState(
        targetValue = if (state == OrbState.LISTENING) 1.5f else 1f,
        animationSpec = tween(300),
        label = "orb-ring-scale",
    )

    val rotation by rememberInfiniteTransition(label = "orb-rotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (state == OrbState.THINKING) 3000 else 8000, easing = CubicBezierEasing(0f, 0f, 1f, 1f)),
            repeatMode = RepeatMode.Restart,
        ),
        label = "orb-rotation",
    )

    val innerPulse = remember { Animatable(0f) }
    LaunchedEffect(state) {
        when (state) {
            OrbState.IDLE -> innerPulse.snapTo(0f)
            OrbState.SPEAKING -> innerPulse.animateTo(
                targetValue = 1f,
                animationSpec = tween(500, easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f))
            )
            else -> innerPulse.animateTo(
                targetValue = 0.5f,
                animationSpec = tween(500)
            )
        }
    }

    val orbColor = when (state) {
        OrbState.IDLE -> colors.orbIdle
        OrbState.LISTENING -> colors.error
        OrbState.PROCESSING -> colors.warning
        OrbState.THINKING -> colors.secondary
        OrbState.SPEAKING -> colors.primary
        OrbState.WAITING_FOR_CONFIRMATION -> colors.secondary
        OrbState.SUCCESS -> colors.success
        OrbState.ERROR -> colors.error
    }

    val canvasSize = size
    Box(
        modifier = modifier
            .size(size * ringScale)
            .border(
                width = 2.dp,
                color = orbColor.copy(alpha = 0.4f * pulseProgress),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(canvasSize)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = canvasSize.toPx() / 2f
            val ringRadius = outerRadius * (0.6f + 0.2f * pulseProgress)

            if (state != OrbState.IDLE) {
                drawCircle(
                    color = orbColor.copy(alpha = 0.15f),
                    radius = ringRadius,
                    style = Stroke(
                        width = 8f,
                        cap = StrokeCap.Round,
                    ),
                )
            }

            val spokes = 12
            val spokeLen = ringRadius * 0.3f
            repeat(spokes) { i ->
                val angleRad = (rotation + i * (360f / spokes)) * Math.PI / 180.0
                val cx = center.x + spokeLen * kotlin.math.cos(angleRad).toFloat()
                val cy = center.y + spokeLen * kotlin.math.sin(angleRad).toFloat()
                val dotRadius = if (state == OrbState.THINKING || state == OrbState.SPEAKING) {
                    4f * pulseProgress
                } else {
                    2f
                }
                if (dotRadius > 0.5f) {
                    drawCircle(
                        color = orbColor,
                        center = Offset(cx, cy),
                        radius = dotRadius,
                    )
                }
            }

            drawCircle(
                color = orbColor,
                radius = outerRadius * (0.4f + 0.1f * innerPulse.value.coerceIn(0f, 1f)),
            )
        }
    }
}

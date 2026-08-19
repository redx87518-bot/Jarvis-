package com.jarvis.launcher.features.voice

import android.os.Bundle
import androidx.hilt.android.AndroidEntryPoint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.jarvis.core.JarvisThemeMode
import com.jarvis.launcher.ui.components.JarvisOrb
import com.jarvis.launcher.ui.components.OrbState
import com.jarvis.launcher.ui.theme.JARVISTheme
import com.jarvis.launcher.ui.theme.jarvisColors
import kotlin.math.sin

enum class VoiceMode { IDLE, LISTENING, PROCESSING, THINKING, SPEAKING }
@AndroidEntryPoint
class VoiceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JARVISTheme(themeMode = JarvisThemeMode.Dark, dynamicColor = false) {
                VoiceScreen(
                    onDismiss = { finish() },
                )
            }
        }
    }
}

@Composable
fun VoiceScreen(
    onDismiss: () -> Unit,
) {
    val colors = jarvisColors()
    var mode by rememberSaveable { mutableStateOf(VoiceMode.IDLE) }
    var recognizedText by rememberSaveable { mutableStateOf("") }
    var spokenText by rememberSaveable { mutableStateOf("How can I help?") }

    val amplitudes = remember { mutableStateOf(List(24) { 0f }) }

    LaunchedEffect(mode) {
        when (mode) {
            VoiceMode.IDLE -> {
                spokenText = ""
                recognizedText = ""
            }
            VoiceMode.LISTENING -> {
                // Simulate listening
                amplitudes.value = List(24) { kotlin.random.Random.nextFloat() * 0.8f }
            }
            VoiceMode.PROCESSING -> {}
            VoiceMode.THINKING -> {
                spokenText = "Let me think about that…"
            }
            VoiceMode.SPEAKING -> {
                spokenText = "I can open that app for you."
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End).padding(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = colors.textSecondary,
                )
            }

            when (mode) {
                VoiceMode.IDLE, VoiceMode.SPEAKING -> {
                    Text(
                        text = spokenText,
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.textPrimary,
                    )
                }
                VoiceMode.LISTENING -> {
                    if (recognizedText.isNotEmpty()) {
                        Text(
                            text = recognizedText,
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary,
                        )
                    } else {
                        Text(
                            text = "Listening…",
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.textPrimary,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    VoiceWaveform(amplitudes = amplitudes.value)
                }
                VoiceMode.PROCESSING -> {
                    Text(
                        text = "Processing…",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.textSecondary,
                    )
                }
                VoiceMode.THINKING -> {
                    Text(
                        text = spokenText,
                        style = MaterialTheme.typography.headlineSmall,
                        color = colors.textPrimary,
                    )
                }
            }

            val orbState = when (mode) {
                VoiceMode.IDLE -> OrbState.IDLE
                VoiceMode.LISTENING -> OrbState.LISTENING
                VoiceMode.PROCESSING -> OrbState.PROCESSING
                VoiceMode.THINKING -> OrbState.THINKING
                VoiceMode.SPEAKING -> OrbState.SPEAKING
            }

            JarvisOrb(state = orbState, size = 100.dp)

            VoiceControls(
                mode = mode,
                onModeChange = { mode = it },
            )
        }
    }
}

@Composable
fun VoiceWaveform(amplitudes: List<Float>) {
    val colors = jarvisColors()
    val transition = rememberInfiniteTransition(label = "voice-waveform")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = CubicBezierEasing(0f, 0f, 1f, 1f)),
            repeatMode = RepeatMode.Restart,
        ),
        label = "voice-phase",
    )

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)) {
        val barCount = amplitudes.size
        val barWidth = size.width / barCount
        for (i in 0 until barCount) {
            val progress = amplitudes[i]
            val barHeight = (progress * size.height * 0.8f).coerceAtLeast(2f)
            val x = i * barWidth + barWidth / 2f
            val y = size.height / 2f - barHeight / 2f
            drawRoundRect(
                color = colors.primary,
                topLeft = androidx.compose.ui.geometry.Offset(x - barWidth * 0.3f, y),
                size = androidx.compose.ui.geometry.Size(barWidth * 0.6f, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f),
            )
        }
    }
}

@Composable
fun VoiceControls(
    mode: VoiceMode,
    onModeChange: (VoiceMode) -> Unit,
) {
    val colors = jarvisColors()

    when (mode) {
        VoiceMode.IDLE -> {
            IconButton(onClick = { onModeChange(VoiceMode.LISTENING) }) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Listen",
                    tint = colors.primary,
                    modifier = Modifier.size(48.dp),
                )
            }
        }
        VoiceMode.LISTENING -> {
            IconButton(onClick = { onModeChange(VoiceMode.IDLE) }) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = colors.error,
                    modifier = Modifier.size(48.dp),
                )
            }
        }
        VoiceMode.SPEAKING -> {
            IconButton(onClick = { onModeChange(VoiceMode.IDLE) }) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(48.dp),
                )
            }
        }
        else -> {
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}

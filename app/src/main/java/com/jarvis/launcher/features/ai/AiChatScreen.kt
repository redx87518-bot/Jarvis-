package com.jarvis.launcher.features.ai

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.BubbleDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.card.Card
import androidx.compose.material3.card.CardColors
import androidx.compose.material3.card.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.jarvis.launcher.features.agent.AgentUiState
import com.jarvis.launcher.features.agent.AiChatViewModel
import com.jarvis.launcher.features.agent.ChatMessageUi
import com.jarvis.launcher.ui.components.JarvisOrb
import com.jarvis.launcher.ui.components.OrbState
import com.jarvis.launcher.ui.theme.jarvisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    onNavigateBack: () -> Unit = {},
) {
    val colors = jarvisColors()
    val viewModel: AiChatViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val agentState by viewModel.agentState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val orbState = when (agentState) {
        AgentUiState.Idle -> OrbState.IDLE
        AgentUiState.Listening -> OrbState.LISTENING
        AgentUiState.Processing -> OrbState.PROCESSING
        AgentUiState.Thinking -> OrbState.THINKING
        AgentUiState.Executing -> OrbState.EXECUTING
        AgentUiState.WaitingForConfirmation -> OrbState.WAITING_FOR_CONFIRMATION
        AgentUiState.Speaking -> OrbState.SPEAKING
        AgentUiState.Error -> OrbState.ERROR
        AgentUiState.Success -> OrbState.SUCCESS
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AiChatTopBar(onNavigateBack = onNavigateBack) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    JarvisOrb(state = orbState, modifier = Modifier.size(24.dp))
                    Text(
                        text = "JARVIS",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary,
                    )
                }
            }

            if (uiState.messages.isEmpty()) {
                EmptyState(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.messages, key = { it.id }) { message ->
                        ChatMessageBubble(message)
                    }
                }
            }

            if (uiState.agentState is AgentUiState.Processing ||
                uiState.agentState is AgentUiState.Executing
            ) {
                TypingIndicator(modifier = Modifier.padding(horizontal = 16.dp))
            }

            ConfirmationBanner(
                confirmation = uiState.confirmation,
                onConfirm = viewModel::confirmAction,
                onCancel = viewModel::cancelConfirmation,
            )

            ChatInputField(
                isLoading = uiState.agentState is AgentUiState.Processing ||
                    uiState.agentState is AgentUiState.Executing,
                onSendMessage = viewModel::sendMessage,
                modifier = Modifier.navigationBarsPadding(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatTopBar(onNavigateBack: () -> Unit, title: @Composable () -> Unit) {
    val colors = jarvisColors()
    androidx.compose.material3.CenterAlignedTopAppBar(
        title = title,
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Back",
                    tint = colors.textSecondary,
                )
            }
        },
        colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = colors.textPrimary,
            navigationIconContentColor = colors.textSecondary,
        ),
    )
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    val colors = jarvisColors()
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "How can I help?",
                style = MaterialTheme.typography.headlineSmall,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessageUi) {
    val colors = jarvisColors()
    val alignment = if (message.isUser) Alignment.TopEnd else Alignment.TopStart
    val bubbleColor = if (message.isUser) colors.primary else colors.surface
    val textColor = if (message.isUser) colors.textPrimary else colors.textPrimary

    Row(
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (!message.isUser) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "JARVIS",
                tint = colors.secondary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(if (message.isUser) 18.dp else 20.dp))
                .widthIn(max = 280.dp),
            colors = CardColors(
                containerColor = bubbleColor,
                contentColor = textColor,
                disabledContainerColor = bubbleColor,
                disabledContentColor = textColor,
            ),
            shape = RoundedCornerShape(if (message.isUser) 18.dp else 20.dp),
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
                modifier = Modifier.padding(12.dp),
            )
        }
        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "You",
                tint = colors.primary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    val colors = jarvisColors()
    val dots = listOf("•", "•", "•")
    val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "typing")
    val offsets = List(3) { index ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, delayMillis = index * 100, easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "dot-$index",
        )
    }
    val visibleDots by rememberUpdatedState(offsets)

    Row(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        dots.forEachIndexed { i, dot ->
            Text(
                text = dot,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
            )
        }
    }
}

@Composable
fun ConfirmationBanner(
    confirmation: com.jarvis.launcher.features.agent.ConfirmationRequest?,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    if (confirmation == null) return
    val colors = jarvisColors()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.3f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Confirmation required",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = colors.warning,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = confirmation.description,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) {
                    Text(text = "Cancel", color = colors.textSecondary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onConfirm) {
                    Text(text = "Confirm", color = colors.primary, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ChatInputField(
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = jarvisColors()
    var text by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            placeholder = {
                Text(
                    text = "Type a message…",
                    color = colors.textSecondary,
                )
            },
            singleLine = false,
            maxLines = 4,
            shape = CircleShape,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (text.isNotBlank() && !isLoading) {
                        keyboardController?.hide()
                        onSendMessage(text.trim())
                        text = ""
                    }
                },
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = colors.primary,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                disabledContainerColor = colors.surface,
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = colors.textSecondary,
                disabledPlaceholderColor = colors.textSecondary,
            ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (isLoading) {
            IconButton(onClick = { /* stop */ }) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = colors.error,
                    modifier = Modifier.size(32.dp),
                )
            }
        } else {
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSendMessage(text.trim())
                        text = ""
                    }
                },
                enabled = text.isNotBlank(),
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (text.isNotBlank()) colors.primary else colors.textSecondary,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

package com.jarvis.launcher.features.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jarvis.launcher.ui.theme.JarvisThemeMode
import com.jarvis.launcher.ui.theme.jarvisColors

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    val colors = jarvisColors()
    val viewModel: SettingsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState()),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                )
            },
            navigationIcon = {
                IconButton(onClick = { /* nav back */ }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textSecondary,
                    )
                }
            },
            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = colors.background,
                titleContentColor = colors.textPrimary,
                navigationIconContentColor = colors.textSecondary,
            ),
        )

        SettingsSection(title = "Appearance") {
            ThemeToggleSetting(
                current = uiState.themeMode,
                onThemeChange = { viewModel.setThemeMode(it) },
            )
            BooleanSetting(
                title = "Dynamic color",
                summary = "Use system dynamic colors",
                value = uiState.dynamicColor,
                onValueChange = viewModel::setDynamicColor,
            )
            BooleanSetting(
                title = "Low-memory mode",
                summary = "Reduce visual effects and AI usage",
                value = uiState.lowMemoryMode,
                onValueChange = viewModel::setLowMemoryMode,
            )
        }

        SettingsSection(title = "AI") {
            ModelProviderSetting(
                current = uiState.aiModelProvider,
                onProviderChange = viewModel::setAiModelProvider,
            )
        }

        SettingsSection(title = "Voice") {
            BooleanSetting(
                title = "Auto-speak responses",
                value = uiState.autoSpeak,
                onValueChange = viewModel::setAutoSpeak,
            )
            BooleanSetting(
                title = "Wake word",
                summary = "Listen for 'Hey JARVIS'",
                value = uiState.wakeWordEnabled,
                onValueChange = viewModel::setWakeWordEnabled,
            )
        }

        SettingsSection(title = "Agent") {
            IntSetting(
                title = "Max retries",
                value = uiState.maxRetries,
                min = 0,
                max = 10,
                onValueChange = { viewModel.setMaxRetries(it) },
            )
            IntSetting(
                title = "Max task duration (sec)",
                value = uiState.maxTaskDurationSec,
                min = 10,
                max = 600,
                onValueChange = { viewModel.setMaxTaskDurationSec(it) },
            )
        }

        SettingsSection(title = "Memory") {
            BooleanSetting(
                title = "Memory enabled",
                summary = "JARVIS remembers your preferences",
                value = uiState.memoryEnabled,
                onValueChange = viewModel::setMemoryEnabled,
            )
            SettingItem(
                title = "Manage memory",
                icon = Icons.Default.Settings,
            ) {
                TextButton(onClick = { }) {
                    Text(text = "Manage", color = colors.primary)
                }
            }
        }

        SettingsSection(title = "Permissions") {
            PermissionSettingItem(
                title = "Accessibility",
                summary = "Screen reading and automation",
                icon = Icons.Default.PhoneAndroid,
            )
            PermissionSettingItem(
                title = "Notifications",
                summary = "Read and respond to notifications",
                icon = Icons.Default.Notifications,
            )
        }

        SettingsSection(title = "Privacy & About") {
            SettingItem(
                title = "Privacy controls",
                icon = Icons.Default.Warning,
            ) { }
            SettingItem(
                title = "Data deletion",
                icon = Icons.Default.Sync,
            ) { }
            SettingItem(
                title = "About",
                icon = Icons.Default.Settings,
                trailing = {
                    Text(
                        text = "1.0.0-alpha",
                        color = colors.textSecondary,
                        fontSize = 14.sp,
                    )
                },
            ) { }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    val colors = jarvisColors()
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.textSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 12.sp,
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
        ) {
            content()
        }
    }
}

@Composable
fun BooleanSetting(
    title: String,
    summary: String = "",
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    val colors = jarvisColors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onValueChange(!value) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
            )
            if (summary.isNotEmpty()) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
        }
        Switch(
            checked = value,
            onCheckedChange = onValueChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.primary,
                uncheckedThumbColor = colors.textSecondary,
            ),
        )
    }
}

@Composable
fun IntSetting(
    title: String,
    value: Int,
    min: Int,
    max: Int,
    onValueChange: (Int) -> Unit,
) {
    val colors = jarvisColors()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = min.toFloat()..max.toFloat(),
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = colors.primary,
                activeTrackColor = colors.primary,
                inactiveTrackColor = colors.surfaceVariant,
            ),
        )
    }
}

@Composable
fun ThemeToggleSetting(
    current: JarvisThemeMode,
    onThemeChange: (JarvisThemeMode) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val colors = jarvisColors()
    val label = when (current) {
        is JarvisThemeMode.System -> "System"
        is JarvisThemeMode.Light -> "Light"
        is JarvisThemeMode.Dark -> "Dark"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textPrimary,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceVariant.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = when (current) {
                        is JarvisThemeMode.System -> Icons.Default.Sync
                        is JarvisThemeMode.Light -> Icons.Default.LightMode
                        is JarvisThemeMode.Dark -> Icons.Default.DarkMode
                    },
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        containerColor = colors.surface,
    ) {
        DropdownMenuItem(onClick = { onThemeChange(JarvisThemeMode.System); expanded = false }) {
            Text(text = "System", color = colors.textPrimary)
        }
        DropdownMenuItem(onClick = { onThemeChange(JarvisThemeMode.Light); expanded = false }) {
            Text(text = "Light", color = colors.textPrimary)
        }
        DropdownMenuItem(onClick = { onThemeChange(JarvisThemeMode.Dark); expanded = false }) {
            Text(text = "Dark", color = colors.textPrimary)
        }
    }
}

@Composable
fun ModelProviderSetting(
    current: String,
    onProviderChange: (String) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val colors = jarvisColors()
    val options = listOf("local" to "Local (offline)", "cloud" to "Cloud (API key)")
    val label = options.find { it.first == current }?.second ?: current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Model provider",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textPrimary,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        containerColor = colors.surface,
    ) {
        options.forEach { option ->
            DropdownMenuItem(onClick = { onProviderChange(option.first); expanded = false }) {
                Text(text = option.second, color = colors.textPrimary)
            }
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    icon: ImageVector,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val colors = jarvisColors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { })
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
            )
        }
        trailing?.invoke(this) ?: Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
fun PermissionSettingItem(
    title: String,
    summary: String,
    icon: ImageVector,
) {
    val colors = jarvisColors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary,
            )
            Text(
                text = summary,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
        TextButton(onClick = { /* open settings */ }) {
            Text(text = "Grant", color = colors.primary)
        }
    }
}

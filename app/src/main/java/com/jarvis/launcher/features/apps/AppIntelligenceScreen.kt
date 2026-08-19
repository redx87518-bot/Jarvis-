import android.graphics.Bitmap
package com.jarvis.launcher.features.apps

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInApp
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jarvis.core.AppCategory
import androidx.hilt.navigation.compose.hiltViewModel
import com.jarvis.launcher.ui.theme.jarvisColors

data class AppIntelligenceUiState(
    val appName: String = "",
    val packageName: String = "",
    val category: String = "",
    val description: String = "",
    val mainFunctions: List<String> = emptyList(),
    val usesInternet: Boolean = false,
    val permissions: List<String> = emptyList(),
    val privacySummary: String = "",
    val similarApps: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val iconBitmap: Bitmap? = null,
    val capabilities: List<Capability> = emptyList(),
    val isLoading: Boolean = true,
)

data class Capability(
    val name: String,
    val enabled: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppIntelligenceScreen(
    packageName: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val colors = jarvisColors()
    var uiState by rememberSaveable { mutableStateOf(AppIntelligenceUiState()) }
    val context = LocalContext.current

    LaunchedEffect(packageName) {
        uiState = uiState.copy(isLoading = true)
        val pm = context.packageManager
        try {
            val info = pm.getApplicationInfo(packageName, 0)
            val label = pm.getApplicationLabel(info).toString()
            val icon = pm.getApplicationIcon(packageName)
            val bmp = android.graphics.Bitmap.createBitmap(
                icon.intrinsicWidth.coerceAtLeast(1),
                icon.intrinsicHeight.coerceAtLeast(1),
                android.graphics.Bitmap.Config.ARGB_8888,
            )
            val canvas = android.graphics.Canvas(bmp)
            icon.setBounds(0, 0, canvas.width, canvas.height)
            icon.draw(canvas)

            uiState = uiState.copy(
                appName = label,
                packageName = packageName,
                category = "App",
                description = "This app is installed on your device.",
                usesInternet = true,
                iconBitmap = bmp,
                isFavorite = false,
                isLoading = false,
                capabilities = listOf(
                    Capability("Open", true),
                    Capability("Navigate", true),
                    Capability("Read permitted UI", true),
                    Capability("Draft replies", false),
                ),
            )
        } catch (e: Exception) {
            uiState = uiState.copy(
                appName = packageName,
                isLoading = false,
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isLoading) "" else uiState.appName,
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
        },
        containerColor = colors.background,
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Loading app info…",
                    color = colors.textSecondary,
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
            ) {
                AppIntelligenceHeader(uiState)
                Spacer(modifier = Modifier.height(16.dp))
                AppIntelligenceCapabilities(uiState.capabilities)
                Spacer(modifier = Modifier.height(16.dp))
                AppIntelligenceDetails(uiState)
                Spacer(modifier = Modifier.height(16.dp))
                AppIntelligenceActions(
                    isFavorite = uiState.isFavorite,
                    onOpenApp = { /* handled by parent */ },
                    onToggleFavorite = { },
                    onAskJarvis = { },
                    onAppInfo = { },
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AppIntelligenceHeader(uiState: AppIntelligenceUiState) {
    val colors = jarvisColors()
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val iconBitmap = uiState.iconBitmap
        if (iconBitmap != null) {
            androidx.compose.ui.graphics.Image(
                bitmap = iconBitmap.asImageBitmap(),
                contentDescription = uiState.appName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.QuestionMark,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(40.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = uiState.appName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = uiState.category,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )
    }
}

@Composable
fun AppIntelligenceCapabilities(capabilities: List<Capability>) {
    val colors = jarvisColors()
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "JARVIS capabilities",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        capabilities.forEach { cap ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp),
            ) {
                Icon(
                    imageVector = if (cap.enabled) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (cap.enabled) "Available" else "Not available",
                    tint = if (cap.enabled) colors.success else colors.textSecondary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = cap.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                )
            }
        }
    }
}

@Composable
fun AppIntelligenceDetails(uiState: AppIntelligenceUiState) {
    val colors = jarvisColors()
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "What it does",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = uiState.description,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )
        if (uiState.usesInternet) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Requires internet",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
        if (uiState.permissions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Permissions: ${uiState.permissions.joinToString()}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }
    }
}

@Composable
fun AppIntelligenceActions(
    isFavorite: Boolean,
    onOpenApp: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAskJarvis: () -> Unit,
    onAppInfo: () -> Unit,
) {
    val colors = jarvisColors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onOpenApp,
            modifier = Modifier
                .clip(CircleShape)
                .weight(1f)
                .height(48.dp),
        ) {
            Text(
                text = "OPEN APP",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }

        TextButton(
            onClick = onAskJarvis,
            modifier = Modifier
                .clip(CircleShape)
                .weight(1f)
                .height(48.dp),
        ) {
            Icon(
                imageVector = Icons.Default.QuestionMark,
                contentDescription = null,
                tint = colors.primary,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Ask JARVIS",
                color = colors.primary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

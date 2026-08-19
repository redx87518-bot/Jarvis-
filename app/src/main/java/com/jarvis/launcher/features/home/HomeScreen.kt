package com.jarvis.launcher.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jarvis.core.AppInfo
import com.jarvis.launcher.ui.components.AppCard
import com.jarvis.launcher.ui.components.BottomNavItem
import com.jarvis.launcher.ui.components.JarvisBottomBar
import com.jarvis.launcher.ui.components.JarvisOrb
import com.jarvis.launcher.ui.components.OrbState
import com.jarvis.launcher.ui.theme.jarvisColors

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onNavigateToApps: () -> Unit,
    onNavigateToAI: () -> Unit,
) {
    val colors = jarvisColors()
    val homeViewModel: HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp),
        ) {
            HomeStatusSection(uiState)
            Spacer(modifier = Modifier.height(16.dp))
            JarvisVoiceButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(80.dp),
                onClick = onNavigateToAI,
            )
            Spacer(modifier = Modifier.height(24.dp))
            QuickSearchBar(
                onSearch = onNavigateToApps,
                onVoiceClick = onNavigateToAI,
            )
            Spacer(modifier = Modifier.height(24.dp))
            FavoritesSection(
                favorites = uiState.favorites,
                onAppClick = { appInfo ->
                    navController.navigate("app_intelligence/${appInfo.packageName}")
                },
            )
            Spacer(modifier = Modifier.height(24.dp))
            RecentAppsSection(
                recentApps = uiState.recentApps,
                onAppClick = { appInfo ->
                    navController.navigate("app_intelligence/${appInfo.packageName}")
                },
            )
            Spacer(modifier = Modifier.height(24.dp))
            AiBriefingSection(
                items = uiState.briefingItems,
                onBriefingClick = { navController.navigate("ai") },
            )
        }

        JarvisBottomBar(
            items = listOf(
                BottomNavItem("Home", Icons.Default.Home, Icons.Default.Home, "home"),
                BottomNavItem("AI", Icons.Default.ChatBubble, Icons.Default.ChatBubble, "ai"),
                BottomNavItem("Apps", Icons.Default.Apps, Icons.Default.Apps, "apps"),
                BottomNavItem("Tasks", Icons.Default.TaskAlt, Icons.Default.TaskAlt, "tasks"),
                BottomNavItem("Settings", Icons.Default.Settings, Icons.Default.Settings, "settings"),
            ),
            selectedRoute = "home",
            onItemSelected = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun HomeStatusSection(uiState: HomeUiState) {
    val colors = jarvisColors()
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = uiState.time,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
            Text(
                text = "Battery ${uiState.batteryLevel}%",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "JARVIS",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = uiState.greeting,
            style = MaterialTheme.typography.headlineMedium,
            color = colors.textSecondary,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            JarvisOrb(
                state = OrbState.IDLE,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "AI STATUS: READY",
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
            )
        }
    }
}

@Composable
fun QuickSearchBar(
    onSearch: () -> Unit,
    onVoiceClick: () -> Unit,
) {
    val colors = jarvisColors()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp)
            .clip(CircleShape)
            .background(colors.surface)
            .border(1.dp, colors.surfaceVariant, CircleShape)
            .clickable(onClick = onSearch),
        contentAlignment = Alignment.CenterStart,
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier
                .size(20.dp)
                .padding(start = 12.dp),
        )
        Text(
            text = "Search apps or ask JARVIS",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            modifier = Modifier.padding(start = 44.dp),
        )
    }
}

@Composable
fun JarvisVoiceButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = jarvisColors()
    IconButton(onClick = onClick, modifier = modifier.size(80.dp)) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(colors.surface.copy(alpha = 0.8f))
                .border(2.dp, colors.secondary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Talk to JARVIS",
                tint = colors.primary,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

@Composable
fun FavoritesSection(
    favorites: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = jarvisColors()
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 0.dp),
        ) {
            lazyItems(favorites, key = { it.packageName }) { app ->
                AppCard(
                    app = app,
                    size = 64.dp,
                    showLabel = true,
                    onClick = onAppClick,
                )
            }
        }
    }
}

@Composable
fun RecentAppsSection(
    recentApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = jarvisColors()
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Recent apps",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (recentApps.isEmpty()) {
            Text(
                text = "No recently used apps",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(0.dp),
            ) {
                items(recentApps.take(8), key = { it.packageName }) { app ->
                    AppCard(
                        app = app,
                        size = 64.dp,
                        showLabel = true,
                        onClick = onAppClick,
                    )
                }
            }
        }
    }
}

@Composable
fun AiBriefingSection(
    items: List<BriefingItem>,
    onBriefingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = jarvisColors()
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "AI Briefing",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            onClick = onBriefingClick,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Quick summary",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textSecondary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                items.forEach { item ->
                    BriefingRow(item)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun BriefingRow(item: BriefingItem) {
    val colors = jarvisColors()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = item.icon)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = item.text,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
        )
    }
}

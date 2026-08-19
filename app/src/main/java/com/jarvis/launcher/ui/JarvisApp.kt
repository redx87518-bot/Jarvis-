package com.jarvis.launcher.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jarvis.launcher.R
import com.jarvis.launcher.features.apps.AppDrawerScreen
import com.jarvis.launcher.features.apps.AppIntelligenceScreen
import com.jarvis.launcher.features.ai.AiChatScreen
import com.jarvis.launcher.features.home.HomeScreen
import com.jarvis.launcher.features.settings.SettingsScreen
import com.jarvis.launcher.features.tasks.TasksScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

data class Screen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
)

@Composable
fun JarvisApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: "home"

    val bottomNavScreens = listOf(
        Screen(
            title = "Home",
            selectedIcon = Icons.Default.Home,
            unselectedIcon = Icons.Default.Home,
            route = "home",
        ),
        Screen(
            title = "AI",
            selectedIcon = Icons.Default.Chat,
            unselectedIcon = Icons.Default.Chat,
            route = "ai",
        ),
        Screen(
            title = "Apps",
            selectedIcon = Icons.Default.Apps,
            unselectedIcon = Icons.Default.Apps,
            route = "apps",
        ),
        Screen(
            title = "Tasks",
            selectedIcon = Icons.Default.TaskAlt,
            unselectedIcon = Icons.Default.TaskAlt,
            route = "tasks",
        ),
        Screen(
            title = "Settings",
            selectedIcon = Icons.Default.Settings,
            unselectedIcon = Icons.Default.Settings,
            route = "settings",
        ),
    )

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier,
    ) {
        composable("home") {
            HomeScreen(
                navController = navController,
                onNavigateToApps = { navController.navigate("apps") },
                onNavigateToAI = { navController.navigate("ai") },
            )
        }
        composable("ai") {
            AiChatScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
        composable("apps") {
            AppDrawerScreen(
                onAppClick = { appInfo ->
                    navController.navigate("app_intelligence/${appInfo.packageName}")
                },
            )
        }
        composable("tasks") {
            TasksScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
        composable("app_intelligence/{packageName}") { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            AppIntelligenceScreen(
                packageName = packageName,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

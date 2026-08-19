package com.jarvis.launcher.features.apps

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jarvis.core.AppInfo
import com.jarvis.core.AppCategory
import com.jarvis.launcher.ui.components.AppCard
import com.jarvis.launcher.ui.components.JarvisSearchBar
import com.jarvis.launcher.ui.theme.jarvisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerScreen(
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = jarvisColors()
    val viewModel: AppDrawerViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val displayApps = if (searchQuery.isBlank()) {
        uiState.filteredApps
    } else {
        viewModel.naturalLanguageSearch(searchQuery)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Apps",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            navigationIcon = {
                IconButton(onClick = { /* nav back handled by caller */ }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.textSecondary,
                    )
                }
            },
            colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = colors.background,
                titleContentColor = colors.textPrimary,
                navigationIconContentColor = colors.textSecondary,
            ),
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Search apps or ask JARVIS",
                    color = colors.textSecondary,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = colors.textSecondary,
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = colors.textSecondary,
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(48.dp),
            singleLine = true,
            shape = CircleShape,
            isError = false,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
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
            ),
        )

        if (uiState.categories.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            CategoryRow(categories = uiState.categories) { category ->
                // TODO: filter by category
            }
        }

        if (displayApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (searchQuery.isNotEmpty()) "No apps found" else "Loading apps...",
                    color = colors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(displayApps, key = { it.packageName }) { app ->
                    AppCard(
                        app = app,
                        size = 72.dp,
                        showLabel = true,
                        onClick = onAppClick,
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryRow(
    categories: List<CategoryCount>,
    onCategoryClick: (AppCategory) -> Unit,
) {
    val colors = jarvisColors()
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            categories.take(5).forEach { item ->
                CategoryChip(item = item, onClick = { onCategoryClick(item.category) })
            }
        }
    }
}

@Composable
fun CategoryChip(
    item: CategoryCount,
    onClick: () -> Unit,
) {
    val colors = jarvisColors()
    Card(
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .size(64.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = item.icon,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = item.category.name.take(3),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
                maxLines = 1,
            )
        }
    }
}

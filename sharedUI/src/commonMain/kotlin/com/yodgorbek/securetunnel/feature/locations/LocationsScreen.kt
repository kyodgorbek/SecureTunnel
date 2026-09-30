package com.yodgorbek.securetunnel.feature.locations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yodgorbek.securetunnel.core.model.ServerSortOption
import com.yodgorbek.securetunnel.designsystem.PrimaryEmerald
import com.yodgorbek.securetunnel.designsystem.ServerListItem
import com.yodgorbek.securetunnel.designsystem.WarningAmber

@Composable
fun LocationsScreen(
    viewModel: LocationsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Text(text = "←", fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "VPN Locations",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${state.displayedServers.size} volunteer relays available",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = { viewModel.onIntent(LocationsIntent.Refresh) },
                modifier = Modifier.size(36.dp)
            ) {
                if (state.isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = PrimaryEmerald
                    )
                } else {
                    Text(text = "🔄", fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Input Bar
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🔍", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = state.filter.searchQuery,
                    onValueChange = { viewModel.onIntent(LocationsIntent.UpdateSearchQuery(it)) },
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(PrimaryEmerald),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (state.filter.searchQuery.isEmpty()) {
                            Text(
                                text = "Search country, city, or IP...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                )
                if (state.filter.searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.onIntent(LocationsIntent.UpdateSearchQuery("")) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text(text = "✕", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter & Sort Horizontal Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterSortChip(
                    title = "Recommended",
                    selected = state.filter.sortOption == ServerSortOption.RECOMMENDED && !state.filter.favoritesOnly,
                    onClick = {
                        viewModel.onIntent(LocationsIntent.ToggleFavoriteFilter(false))
                        viewModel.onIntent(LocationsIntent.ChangeSortOption(ServerSortOption.RECOMMENDED))
                    }
                )
            }
            item {
                FilterSortChip(
                    title = "Lowest Ping ⚡",
                    selected = state.filter.sortOption == ServerSortOption.LOWEST_PING,
                    onClick = { viewModel.onIntent(LocationsIntent.ChangeSortOption(ServerSortOption.LOWEST_PING)) }
                )
            }
            item {
                FilterSortChip(
                    title = "Highest Speed 🚀",
                    selected = state.filter.sortOption == ServerSortOption.HIGHEST_SPEED,
                    onClick = { viewModel.onIntent(LocationsIntent.ChangeSortOption(ServerSortOption.HIGHEST_SPEED)) }
                )
            }
            item {
                FilterSortChip(
                    title = "Favorites ★",
                    selected = state.filter.favoritesOnly,
                    onClick = { viewModel.onIntent(LocationsIntent.ToggleFavoriteFilter(!state.filter.favoritesOnly)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Server List
        if (state.displayedServers.isEmpty() && !state.isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🌐", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No VPN servers matched",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try adjusting your search or filter options",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Recommended banner card at top if available
                state.recommendedServer?.let { rec ->
                    if (state.filter.searchQuery.isEmpty() && !state.filter.favoritesOnly) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = PrimaryEmerald.copy(alpha = 0.1f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryEmerald.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "✨", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Recommended node auto-calculated from lowest latency & bandwidth.",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = PrimaryEmerald
                                    )
                                }
                            }
                        }
                    }
                }

                items(state.displayedServers, key = { it.id }) { server ->
                    val isSelected = state.selectedServer?.id == server.id
                    ServerListItem(
                        server = server,
                        isSelected = isSelected,
                        onSelect = { viewModel.onIntent(LocationsIntent.SelectServer(server)) },
                        onToggleFavorite = { viewModel.onIntent(LocationsIntent.ToggleServerFavorite(server.id)) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun FilterSortChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) PrimaryEmerald else MaterialTheme.colorScheme.surface,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) androidx.compose.ui.graphics.Color.Black else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

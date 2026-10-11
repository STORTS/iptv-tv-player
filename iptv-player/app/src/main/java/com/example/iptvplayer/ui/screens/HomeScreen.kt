package com.example.iptvplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.*
import com.example.iptvplayer.model.Channel
import com.example.iptvplayer.ui.components.ChannelCard
import com.example.iptvplayer.ui.viewmodel.UiState

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: UiState,
    onSearchChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onChannelSelected: (Channel) -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1116))
            .padding(horizontal = 48.dp, vertical = 24.dp)
    ) {
        when (uiState) {
            is UiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Loading Channels from M3U Playlist...",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }
            is UiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: ${uiState.message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text(text = "Retry")
                    }
                }
            }
            is UiState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Top Bar: Title & Search Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live TV Guide",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )

                        Box(
                            modifier = Modifier
                                .width(320.dp)
                                .background(Color(0xFF1E232A), MaterialTheme.shapes.small)
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            if (uiState.searchQuery.isEmpty()) {
                                Text(
                                    text = "Search channels...",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            BasicTextField(
                                value = uiState.searchQuery,
                                onValueChange = onSearchChanged,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                                cursorBrush = SolidColor(Color.White),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Categories List
                    TvLazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(uiState.categories) { category ->
                            FilterChip(
                                selected = category == uiState.selectedCategory,
                                onClick = { onCategorySelected(category) }
                            ) {
                                Text(text = category)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Channel Grid
                    if (uiState.channels.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No channels match your criteria",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        TvLazyVerticalGrid(
                            columns = TvGridCells.Fixed(5),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 32.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.channels,
                                key = { it.id }
                            ) { channel ->
                                ChannelCard(
                                    channel = channel,
                                    onClick = { onChannelSelected(channel) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

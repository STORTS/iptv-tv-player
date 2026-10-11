package com.example.iptvplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.example.iptvplayer.model.Channel
import com.example.iptvplayer.ui.screens.HomeScreen
import com.example.iptvplayer.ui.screens.PlayerScreen
import com.example.iptvplayer.ui.viewmodel.ChannelViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                val viewModel: ChannelViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var currentPlayingChannel by remember { mutableStateOf<Channel?>(null) }

                if (currentPlayingChannel != null) {
                    PlayerScreen(
                        channel = currentPlayingChannel!!,
                        onBackPress = { currentPlayingChannel = null }
                    )
                } else {
                    HomeScreen(
                        uiState = uiState,
                        onSearchChanged = viewModel::onSearchQueryChanged,
                        onCategorySelected = viewModel::onCategorySelected,
                        onChannelSelected = { channel -> currentPlayingChannel = channel },
                        onRetry = viewModel::loadChannels
                    )
                }
            }
        }
    }
}

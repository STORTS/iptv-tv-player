package com.example.iptvplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iptvplayer.data.ChannelRepository
import com.example.iptvplayer.model.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState {
    object Loading : UiState
    data class Success(
        val categories: List<String>,
        val selectedCategory: String,
        val channels: List<Channel>,
        val searchQuery: String
    ) : UiState
    data class Error(val message: String) : UiState
}

class ChannelViewModel(
    private val repository: ChannelRepository = ChannelRepository()
) : ViewModel() {

    private val _rawChannels = MutableStateFlow<List<Channel>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("All")

    val uiState: StateFlow<UiState> = combine(
        _rawChannels,
        _isLoading,
        _errorMessage,
        _searchQuery,
        _selectedCategory
    ) { channels, loading, error, query, category ->
        when {
            loading -> UiState.Loading
            error != null -> UiState.Error(error)
            else -> {
                val categories = listOf("All") + channels.map { it.group }.distinct().sorted()
                val filtered = channels.asSequence()
                    .filter { if (category == "All") true else it.group.equals(category, ignoreCase = true) }
                    .filter { if (query.isBlank()) true else it.name.contains(query, ignoreCase = true) }
                    .toList()

                UiState.Success(
                    categories = categories,
                    selectedCategory = category,
                    channels = filtered,
                    searchQuery = query
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    init {
        loadChannels()
    }

    fun loadChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val list = repository.fetchPlaylist("https://iptv-org.github.io/iptv/index.m3u")
                _rawChannels.value = list
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to load channel guide"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }
    fun onCategorySelected(category: String) { _selectedCategory.value = category }
}

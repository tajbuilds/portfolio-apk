package com.taj.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.taj.portfolio.data.PortfolioRepository
import com.taj.portfolio.data.WorkDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WorkDetailUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val syncMessage: String? = null,
    val item: WorkDetail? = null,
)

class WorkDetailViewModel(
    private val repository: PortfolioRepository,
    private val slug: String,
) : ViewModel() {
    private val _state = MutableStateFlow(WorkDetailUiState())
    val state: StateFlow<WorkDetailUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val cachedResult = repository.getCachedDetail(slug)
            val cached = cachedResult.value?.item
            if (cached != null) {
                _state.value = WorkDetailUiState(loading = true, item = cached)
            } else {
                _state.value = WorkDetailUiState(loading = true)
            }

            val refreshed = runCatching { repository.refreshDetail(slug).item }.getOrNull()
            _state.value = when {
                refreshed != null -> WorkDetailUiState(loading = false, item = refreshed)
                cached != null -> WorkDetailUiState(
                    loading = false,
                    item = cached,
                    syncMessage = if (cachedResult.isStale) {
                        "Couldn't refresh this case study. Showing stale cached content."
                    } else {
                        "Couldn't refresh this case study. Showing cached content."
                    },
                )
                else -> WorkDetailUiState(loading = false, error = "Failed to load work item")
            }
        }
    }
}

class WorkDetailViewModelFactory(
    private val repository: PortfolioRepository,
    private val slug: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkDetailViewModel(repository, slug) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

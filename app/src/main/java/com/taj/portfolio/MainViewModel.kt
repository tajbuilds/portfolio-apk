package com.taj.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taj.portfolio.data.About
import com.taj.portfolio.data.Cta
import com.taj.portfolio.data.Contact
import com.taj.portfolio.data.PortfolioRepository
import com.taj.portfolio.data.Profile
import com.taj.portfolio.data.WorkSummary
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val profile: Profile? = null,
    val cta: Cta? = null,
    val featuredWork: List<WorkSummary> = emptyList(),
    val work: List<WorkSummary> = emptyList(),
    val about: About? = null,
    val contact: Contact? = null,
)

class MainViewModel(private val repository: PortfolioRepository) : ViewModel() {
    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val cachedHome = async { repository.getCachedHome() }
            val cachedWork = async { repository.getCachedWork() }
            val cachedAbout = async { repository.getCachedAbout() }
            val cachedContact = async { repository.getCachedContact() }

            val home = cachedHome.await()
            val work = cachedWork.await()
            val about = cachedAbout.await()
            val contact = cachedContact.await()

            _state.value = MainUiState(
                loading = true,
                profile = home?.profile,
                cta = home?.cta,
                featuredWork = home?.featuredWork ?: emptyList(),
                work = work?.items ?: emptyList(),
                about = about?.about,
                contact = contact?.contact,
            )

            val refreshedHome = runCatching { repository.refreshHome() }.getOrNull()
            val refreshedWork = runCatching { repository.refreshWork() }.getOrNull()
            val refreshedAbout = runCatching { repository.refreshAbout() }.getOrNull()
            val refreshedContact = runCatching { repository.refreshContact() }.getOrNull()

            val hasAnyData = (refreshedHome ?: home) != null || (refreshedWork ?: work) != null

            _state.value = MainUiState(
                loading = false,
                error = if (!hasAnyData) "Unable to load portfolio data" else null,
                profile = (refreshedHome ?: home)?.profile,
                cta = (refreshedHome ?: home)?.cta,
                featuredWork = (refreshedHome ?: home)?.featuredWork ?: emptyList(),
                work = (refreshedWork ?: work)?.items ?: emptyList(),
                about = (refreshedAbout ?: about)?.about,
                contact = (refreshedContact ?: contact)?.contact,
            )
        }
    }
}

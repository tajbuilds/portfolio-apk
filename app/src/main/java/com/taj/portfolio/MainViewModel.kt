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
    val syncMessage: String? = null,
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
            val cachedHomeResult = async { repository.getCachedHome() }
            val cachedWorkResult = async { repository.getCachedWork() }
            val cachedAboutResult = async { repository.getCachedAbout() }
            val cachedContactResult = async { repository.getCachedContact() }

            val cachedHome = cachedHomeResult.await()
            val cachedWork = cachedWorkResult.await()
            val cachedAbout = cachedAboutResult.await()
            val cachedContact = cachedContactResult.await()

            val home = cachedHome.value
            val work = cachedWork.value
            val about = cachedAbout.value
            val contact = cachedContact.value

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

            val refreshFailures = buildList {
                if (refreshedHome == null) add("Home")
                if (refreshedWork == null) add("Work")
                if (refreshedAbout == null) add("About")
                if (refreshedContact == null) add("Contact")
            }

            val staleSections = buildList {
                if (cachedHome.value != null && cachedHome.isStale) add("Home")
                if (cachedWork.value != null && cachedWork.isStale) add("Work")
                if (cachedAbout.value != null && cachedAbout.isStale) add("About")
                if (cachedContact.value != null && cachedContact.isStale) add("Contact")
            }

            val hasAnyData = (refreshedHome ?: home) != null || (refreshedWork ?: work) != null
            val syncMessage = when {
                refreshFailures.isEmpty() && staleSections.isEmpty() -> null
                refreshFailures.isEmpty() && staleSections.isNotEmpty() ->
                    "Cached data currently shown for: ${staleSections.joinToString(", ")}."
                hasAnyData -> "Couldn't refresh ${refreshFailures.joinToString(", ")}. Showing cached data."
                else -> null
            }

            _state.value = MainUiState(
                loading = false,
                error = if (!hasAnyData) "Unable to load portfolio data" else null,
                syncMessage = syncMessage,
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

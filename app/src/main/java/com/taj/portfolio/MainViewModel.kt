package com.taj.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taj.portfolio.data.PortfolioRepository
import com.taj.portfolio.ui.model.AboutUi
import com.taj.portfolio.ui.model.ContactUi
import com.taj.portfolio.ui.model.CtaUi
import com.taj.portfolio.ui.model.ProfileUi
import com.taj.portfolio.ui.model.WorkSummaryUi
import com.taj.portfolio.ui.model.toUi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val syncMessage: String? = null,
    val apiVersion: String? = null,
    val latestGeneratedAt: String? = null,
    val profile: ProfileUi? = null,
    val cta: CtaUi? = null,
    val featuredWork: List<WorkSummaryUi> = emptyList(),
    val work: List<WorkSummaryUi> = emptyList(),
    val about: AboutUi? = null,
    val contact: ContactUi? = null,
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
                apiVersion = home?.version ?: work?.version ?: about?.version ?: contact?.version,
                latestGeneratedAt = listOfNotNull(
                    cachedHome.generatedAt,
                    cachedWork.generatedAt,
                    cachedAbout.generatedAt,
                    cachedContact.generatedAt,
                ).maxOrNull(),
                profile = home?.profile?.toUi(),
                cta = home?.cta?.toUi(),
                featuredWork = home?.featuredWork?.map { it.toUi() } ?: emptyList(),
                work = work?.items?.map { it.toUi() } ?: emptyList(),
                about = about?.about?.toUi(),
                contact = contact?.contact?.toUi(),
            )

            val refreshedHome = runCatching { repository.refreshHome() }.getOrNull()
            val refreshedWork = runCatching { repository.refreshWork() }.getOrNull()
            val refreshedAbout = runCatching { repository.refreshAbout() }.getOrNull()
            val refreshedContact = runCatching { repository.refreshContact() }.getOrNull()

            val hasAnyData = (refreshedHome ?: home) != null || (refreshedWork ?: work) != null
            val latestGeneratedAt = listOfNotNull(
                refreshedHome?.generatedAt ?: cachedHome.generatedAt,
                refreshedWork?.generatedAt ?: cachedWork.generatedAt,
                refreshedAbout?.generatedAt ?: cachedAbout.generatedAt,
                refreshedContact?.generatedAt ?: cachedContact.generatedAt,
            ).maxOrNull()

            val syncMessage = null

            _state.value = MainUiState(
                loading = false,
                error = if (!hasAnyData) "Unable to load portfolio data" else null,
                syncMessage = syncMessage,
                apiVersion = refreshedHome?.version ?: refreshedWork?.version ?: refreshedAbout?.version ?: refreshedContact?.version
                    ?: home?.version ?: work?.version ?: about?.version ?: contact?.version,
                latestGeneratedAt = latestGeneratedAt,
                profile = (refreshedHome ?: home)?.profile?.toUi(),
                cta = (refreshedHome ?: home)?.cta?.toUi(),
                featuredWork = (refreshedHome ?: home)?.featuredWork?.map { it.toUi() } ?: emptyList(),
                work = (refreshedWork ?: work)?.items?.map { it.toUi() } ?: emptyList(),
                about = (refreshedAbout ?: about)?.about?.toUi(),
                contact = (refreshedContact ?: contact)?.contact?.toUi(),
            )
        }
    }

    fun clearCacheAndRefresh() {
        viewModelScope.launch {
            repository.clearCache()
            refresh()
        }
    }
}

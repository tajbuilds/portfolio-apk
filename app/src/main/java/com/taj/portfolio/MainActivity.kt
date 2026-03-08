package com.taj.portfolio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.google.gson.Gson
import com.tajbuilds.portfolio.BuildConfig
import com.taj.portfolio.data.PortfolioApi
import com.taj.portfolio.data.PortfolioRepository
import com.taj.portfolio.data.cache.AppDatabase
import com.taj.portfolio.ui.model.WorkDetailUi
import com.taj.portfolio.ui.model.WorkSummaryUi
import com.taj.portfolio.ui.theme.PortfolioTheme
import com.taj.portfolio.ui.theme.ThemeMode
import com.taj.portfolio.ui.theme.ThemePreferences
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val gson = Gson()
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "portfolio.db").build()
        val repository = PortfolioRepository(
            api = retrofit.create(PortfolioApi::class.java),
            cacheDao = db.cacheDao(),
            gson = gson,
        )
        val themePreferences = ThemePreferences(applicationContext)

        setContent {
            var themeMode by remember { mutableStateOf(themePreferences.getThemeMode()) }
            PortfolioTheme(themeMode = themeMode) {
                val mainVm: MainViewModel = viewModel(factory = MainViewModelFactory(repository))
                App(
                    mainVm = mainVm,
                    repository = repository,
                    themeMode = themeMode,
                    onThemeModeChange = { mode ->
                        themeMode = mode
                        themePreferences.setThemeMode(mode)
                    },
                )
            }
        }
    }
}

private data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

private val topDestinations = listOf(
    TopLevelDestination("home", "Home", Icons.Default.Home),
    TopLevelDestination("work", "Work", Icons.Default.Work),
    TopLevelDestination("about", "About", Icons.Default.AccountCircle),
    TopLevelDestination("contact", "Contact", Icons.Default.Mail),
    TopLevelDestination("settings", "Settings", Icons.Default.Settings),
)

@Composable
private fun App(
    mainVm: MainViewModel,
    repository: PortfolioRepository,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    val navController = rememberNavController()
    val state by mainVm.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { if (showBottomBar()) BottomNavBar(navController) },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding),
        ) {
            composable("home") {
                HomeScreen(
                    state = state,
                    onRetry = mainVm::refresh,
                    onPullToRefresh = mainVm::refresh,
                    onOpenWork = { navController.navigate("work") },
                    onOpenWorkItem = { slug -> navController.navigate("work/$slug") },
                )
            }
            composable("work") {
                WorkScreen(
                    state = state,
                    onRetry = mainVm::refresh,
                    onPullToRefresh = mainVm::refresh,
                    onOpenWorkItem = { slug -> navController.navigate("work/$slug") },
                )
            }
            composable(
                route = "work/{slug}",
                arguments = listOf(navArgument("slug") { type = NavType.StringType }),
            ) { backStackEntry ->
                val slug = backStackEntry.arguments?.getString("slug").orEmpty()
                val detailVm: WorkDetailViewModel = viewModel(
                    factory = WorkDetailViewModelFactory(repository, slug),
                    key = "work-detail-$slug",
                )
                val detailState by detailVm.state.collectAsStateWithLifecycle()

                WorkDetailScreen(
                    slug = slug,
                    state = detailState,
                    onRetry = detailVm::refresh,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("about") {
                AboutScreen(
                    state = state,
                    onRetry = mainVm::refresh,
                    onPullToRefresh = mainVm::refresh,
                )
            }
            composable("contact") {
                ContactScreen(
                    state = state,
                    repository = repository,
                    onRetry = mainVm::refresh,
                    onPullToRefresh = mainVm::refresh,
                    onOpenContactForm = { url ->
                        navController.navigate("contact/form/${Uri.encode(url)}")
                    },
                )
            }
            composable(
                route = "contact/form/{url}",
                arguments = listOf(navArgument("url") { type = NavType.StringType }),
            ) { backStackEntry ->
                val decodedUrl = Uri.decode(backStackEntry.arguments?.getString("url").orEmpty())
                ContactFormScreen(
                    url = decodedUrl,
                    onBack = { navController.popBackStack() },
                )
            }
            composable("settings") {
                SettingsScreen(
                    state = state,
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange,
                )
            }
        }
    }
}

@Composable
private fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        topDestinations.forEach { destination ->
            val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    val didPop = navController.popBackStack(destination.route, inclusive = false)
                    if (!didPop) {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(imageVector = destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
            )
        }
    }
}

@Composable
private fun HomeScreen(
    state: MainUiState,
    onRetry: () -> Unit,
    onPullToRefresh: () -> Unit,
    onOpenWork: () -> Unit,
    onOpenWorkItem: (String) -> Unit,
) {
    if (state.loading && state.profile == null) return FullScreenLoading("Loading portfolio...")
    if (state.error != null && state.profile == null) return FullScreenError(state.error, onRetry)

    PullRefreshContainer(refreshing = state.loading, onRefresh = onPullToRefresh) {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (!state.syncMessage.isNullOrBlank()) {
            item { SyncBanner(message = state.syncMessage) }
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surface,
                                ),
                            ),
                        )
                        .padding(18.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = state.profile?.avatarUrl?.let(::absoluteUrl),
                            contentDescription = state.profile?.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
                        )
                        Column {
                            Text(
                                state.profile?.name.orEmpty(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(state.profile?.role.orEmpty(), style = MaterialTheme.typography.titleMedium)
                            Text(
                                state.profile?.location.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Text(state.profile?.tagline.orEmpty(), modifier = Modifier.padding(top = 8.dp))
                    Text(
                        "Shipping reliable product experiences across web, mobile, and ML workflows.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Featured Work", style = MaterialTheme.typography.titleLarge)
                Text("See all", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { onOpenWork() })
            }
        }
            items(state.featuredWork) { item -> WorkCard(item = item, onClick = { onOpenWorkItem(item.slug) }) }
        }
    }
}

@Composable
private fun WorkScreen(
    state: MainUiState,
    onRetry: () -> Unit,
    onPullToRefresh: () -> Unit,
    onOpenWorkItem: (String) -> Unit,
) {
    if (state.loading && state.work.isEmpty()) return FullScreenLoading("Loading projects...")
    if (state.error != null && state.work.isEmpty()) return FullScreenError(state.error, onRetry)

    PullRefreshContainer(refreshing = state.loading, onRefresh = onPullToRefresh) {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (!state.syncMessage.isNullOrBlank()) {
                item { SyncBanner(message = state.syncMessage) }
            }
            item {
                Text("Work", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Case studies and architecture outcomes", style = MaterialTheme.typography.bodyMedium)
            }
            items(state.work) { item -> WorkCard(item = item, onClick = { onOpenWorkItem(item.slug) }) }
        }
    }
}

@Composable
private fun WorkCard(item: WorkSummaryUi, onClick: () -> Unit) {
    val imageLoader = rememberSvgImageLoader()
    val hasGenericAvatarCover =
        item.coverImageUrl.contains("tajinder-singh-portrait", ignoreCase = true)
    val coverModel = item.coverImageUrl.takeIf { it.isNotBlank() && !hasGenericAvatarCover }?.let(::absoluteUrl)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (coverModel != null) {
                AsyncImage(
                    imageLoader = imageLoader,
                    model = coverModel,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surface,
                                ),
                            ),
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                item.summary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(item.role, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(item.timeline, style = MaterialTheme.typography.bodySmall)
            Text("Updated ${item.updatedAt}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item.tags.forEach { tag -> AssistChip(onClick = {}, label = { Text(tag) }) }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("View case study", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun WorkDetailScreen(slug: String, state: WorkDetailUiState, onRetry: () -> Unit, onBack: () -> Unit) {
    if (state.loading && state.item == null) return FullScreenLoading("Loading $slug...")
    if (state.error != null && state.item == null) return FullScreenError(state.error, onRetry)
    state.item?.let { WorkDetailContent(item = it, onBack = onBack, syncMessage = state.syncMessage) }
}

@Composable
private fun WorkDetailContent(item: WorkDetailUi, onBack: () -> Unit, syncMessage: String?) {
    val context = LocalContext.current
    val imageLoader = rememberSvgImageLoader()
    val hasGenericAvatarCover = item.coverImageUrl.contains("tajinder-singh-portrait", ignoreCase = true)
    val coverModel = item.coverImageUrl.takeIf { it.isNotBlank() && !hasGenericAvatarCover }?.let(::absoluteUrl)
    val sections = listOf(
        "Context" to item.sections?.context,
        "Constraints" to item.sections?.constraints,
        "Approach" to item.sections?.approach,
        "Outcome" to item.sections?.outcome,
        "Learnings" to item.sections?.learnings,
    ).filter { (_, content) -> !content.isNullOrBlank() }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (!syncMessage.isNullOrBlank()) {
            item { SyncBanner(message = syncMessage) }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                Spacer(Modifier.width(8.dp))
                Text("Back", color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            if (coverModel != null) {
                AsyncImage(
                    imageLoader = imageLoader,
                    model = coverModel,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(item.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(item.summary, style = MaterialTheme.typography.bodyLarge)
            Text(item.role, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Text(item.timeline, style = MaterialTheme.typography.bodyMedium)
            Text(
                "Published ${item.publishedAt} • Updated ${item.updatedAt}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item.tags.forEach { tag -> AssistChip(onClick = {}, label = { Text(tag) }) }
            }
        }
        items(sections) { (title, content) -> SectionCard(title = title, content = content.orEmpty()) }
        item {
            item.links?.let { links ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    links.liveDemo?.takeIf { it.isNotBlank() }?.let { liveDemo ->
                        Button(onClick = { openUrl(context, liveDemo) }) {
                            Text("Live Demo")
                        }
                    }
                    links.repository?.takeIf { it.isNotBlank() }?.let { repository ->
                        Button(onClick = { openUrl(context, repository) }) {
                            Text("Repository")
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        "Case Study Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "Open the full web version for complete narrative, media, and technical details.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedButton(onClick = { openUrl(context, absoluteUrl("/work/${item.slug}/")) }) {
                        Text("Open Full Web Case Study")
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: String) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(cleanDetailText(content), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun AboutScreen(
    state: MainUiState,
    onRetry: () -> Unit,
    onPullToRefresh: () -> Unit,
) {
    val context = LocalContext.current
    if (state.loading && state.about == null) return FullScreenLoading("Loading about...")
    if (state.error != null && state.about == null) return FullScreenError(state.error, onRetry)

    val about = state.about
    PullRefreshContainer(refreshing = state.loading, onRefresh = onPullToRefresh) {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (!state.syncMessage.isNullOrBlank()) {
                item { SyncBanner(message = state.syncMessage) }
            }
            item {
                AsyncImage(
                    model = about?.avatarUrl?.let(::absoluteUrl),
                    contentDescription = about?.name,
                    modifier = Modifier
                        .size(88.dp)
                        .padding(bottom = 10.dp),
                )
                Text(about?.name.orEmpty(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(about?.headline.orEmpty(), style = MaterialTheme.typography.titleMedium)
                Text(about?.bio.orEmpty(), modifier = Modifier.padding(top = 8.dp))
            }
            item {
                Text("Skills", style = MaterialTheme.typography.titleMedium)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    about?.skills?.forEach { skill ->
                        AssistChip(onClick = {}, label = { Text(skill) }, modifier = Modifier.padding(end = 8.dp))
                    }
                }
            }
            item {
                Text("Focus Areas", style = MaterialTheme.typography.titleMedium)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { about?.focusAreas?.forEach { area -> Text("- $area") } }
            }
            item {
                val socialLinks = about?.social.orEmpty()
                if (socialLinks.isNotEmpty()) {
                    Text("Social", style = MaterialTheme.typography.titleMedium)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        socialLinks.forEach { social ->
                            Row(
                                modifier = Modifier.clickable { openUrl(context, social.url) },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(social.label, color = MaterialTheme.colorScheme.primary)
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactScreen(
    state: MainUiState,
    repository: PortfolioRepository,
    onRetry: () -> Unit,
    onPullToRefresh: () -> Unit,
    onOpenContactForm: (String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    if (state.loading && state.contact == null) return FullScreenLoading("Loading contact...")
    if (state.error != null && state.contact == null) return FullScreenError(state.error, onRetry)

    val contact = state.contact
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("") }
    var submitStatus by rememberSaveable { mutableStateOf<String?>(null) }
    var submitError by rememberSaveable { mutableStateOf(false) }
    var submitting by rememberSaveable { mutableStateOf(false) }

    val emailRegex = remember { Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") }
    val wordCount = remember(message) { message.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size }

    PullRefreshContainer(refreshing = state.loading, onRefresh = onPullToRefresh) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                if (!state.syncMessage.isNullOrBlank()) {
                    SyncBanner(message = state.syncMessage)
                }
            }
            item {
                Text("Contact", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Reach out directly or open the embedded form.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(contact?.email.orEmpty(), style = MaterialTheme.typography.bodyLarge)
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${contact?.email.orEmpty()}"))
                            runCatching { context.startActivity(intent) }
                        }) {
                            Icon(Icons.Default.Mail, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Email Me")
                        }
                    }
                }
            }
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Send Message", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = name,
                            onValueChange = { if (it.length <= 120) name = it },
                            label = { Text("Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { if (it.length <= 190) email = it },
                            label = { Text("Email") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = message,
                            onValueChange = { if (it.length <= 2500) message = it },
                            label = { Text("Message") },
                            minLines = 5,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            "Minimum 30 characters and 29 words. Current words: $wordCount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(
                            onClick = {
                                val cleanName = name.trim()
                                val cleanEmail = email.trim()
                                val cleanMessage = message.trim()
                                when {
                                    cleanName.length < 2 -> {
                                        submitError = true
                                        submitStatus = "Please enter your full name (at least 2 characters)."
                                    }
                                    !emailRegex.matches(cleanEmail) -> {
                                        submitError = true
                                        submitStatus = "Please enter a valid email address."
                                    }
                                    cleanMessage.length < 30 -> {
                                        submitError = true
                                        submitStatus = "Please enter a message with at least 30 characters."
                                    }
                                    wordCount < 29 -> {
                                        submitError = true
                                        submitStatus = "Please provide more detail (minimum 29 words)."
                                    }
                                    else -> {
                                        submitting = true
                                        submitError = false
                                        submitStatus = "Sending..."
                                        scope.launch {
                                            val result = runCatching {
                                                repository.submitContact(
                                                    com.taj.portfolio.data.ContactSubmitRequest(
                                                        name = cleanName,
                                                        email = cleanEmail,
                                                        message = cleanMessage,
                                                    ),
                                                )
                                            }.getOrNull()
                                            submitting = false
                                            if (result?.ok == true) {
                                                submitError = false
                                                submitStatus = result.message
                                                name = ""
                                                email = ""
                                                message = ""
                                            } else {
                                                submitError = true
                                                submitStatus = result?.message ?: "Unable to send message right now."
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = !submitting,
                        ) {
                            Text(if (submitting) "Sending..." else "Send Message")
                        }
                        submitStatus?.let { status ->
                            Text(
                                status,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (submitError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            )
                        }
                        OutlinedButton(onClick = { onOpenContactForm(absoluteUrl("/contact/")) }) {
                            Text("Open Web Form Instead")
                        }
                    }
                }
            }

            item {
                if (contact?.turnstileRequired == true) {
                    Text("Form submission is protected by Turnstile.", style = MaterialTheme.typography.bodySmall)
                }
            }

            items(contact?.links.orEmpty()) { link ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openUrl(context, link.url) },
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(link.label, color = MaterialTheme.colorScheme.primary)
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    state: MainUiState,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Customize appearance and app behavior.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeMode.entries.forEach { mode ->
                            FilterChip(
                                selected = themeMode == mode,
                                onClick = { onThemeModeChange(mode) },
                                label = { Text(mode.label()) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (mode) {
                                            ThemeMode.SYSTEM -> Icons.Default.BrightnessAuto
                                            ThemeMode.LIGHT -> Icons.Default.Brightness7
                                            ThemeMode.DARK -> Icons.Default.Brightness4
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("App", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                    Text("API contract ${state.apiVersion ?: "unknown"}")
                    Text("API ${BuildConfig.API_BASE_URL}")
                    state.latestGeneratedAt?.let { generatedAt ->
                        Text("Latest data generated at $generatedAt")
                    }
                    OutlinedButton(onClick = { openUrl(context, BuildConfig.API_BASE_URL) }) {
                        Text("Open Website")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PullRefreshContainer(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val pullOffset = (pullToRefreshState.distanceFraction * 96f).coerceAtLeast(0f).dp
    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize(),
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = refreshing,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = pullOffset),
        ) {
            content()
        }
    }
}

@Composable
private fun rememberSvgImageLoader(): ImageLoader {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        sharedSvgImageLoader ?: ImageLoader.Builder(context)
            .components { add(SvgDecoder.Factory()) }
            .build()
            .also { sharedSvgImageLoader = it }
    }
}

private var sharedSvgImageLoader: ImageLoader? = null

@Composable
private fun ContactFormScreen(url: String, onBack: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.clickable(onClick = onBack),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                Spacer(Modifier.width(6.dp))
                Text("Back", color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { openUrl(context, url) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Open in browser",
                )
            }
        }
        AndroidView(
            factory = { viewContext ->
                WebView(viewContext).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                }
            },
            update = { webView -> webView.loadUrl(url) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun SyncBanner(message: String?) {
    if (message.isNullOrBlank()) return
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun FullScreenLoading(label: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(28.dp))
        Text(label, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun FullScreenError(message: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Failed to load", style = MaterialTheme.typography.titleLarge)
        Text(message.orEmpty(), modifier = Modifier.padding(top = 8.dp))
        Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) { Text("Retry") }
    }
}

private fun absoluteUrl(pathOrUrl: String): String {
    return resolveUrl(BuildConfig.API_BASE_URL, pathOrUrl)
}

private fun absoluteContactFormUrl(pathOrUrl: String): String {
    return resolveContactFormUrl(BuildConfig.API_BASE_URL, pathOrUrl)
}

private fun openUrl(context: android.content.Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    runCatching { context.startActivity(intent) }
}

private fun ThemeMode.label(): String = when (this) {
    ThemeMode.SYSTEM -> "System"
    ThemeMode.LIGHT -> "Light"
    ThemeMode.DARK -> "Dark"
}

private fun cleanDetailText(value: String): String {
    return value
        .replace("**", "")
        .replace("__", "")
        .replace("`", "")
        .replace(Regex("(?m)^#{1,6}\\s*"), "")
        .replace(Regex("(?m)^[-*]\\s+"), "")
        .replace(Regex("\\n{3,}"), "\n\n")
        .trim()
}

@Composable
private fun showBottomBar(): Boolean {
    return true
}




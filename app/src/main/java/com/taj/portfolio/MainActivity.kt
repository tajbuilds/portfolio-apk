package com.taj.portfolio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.google.gson.Gson
import com.tajbuilds.portfolio.BuildConfig
import com.taj.portfolio.data.PortfolioApi
import com.taj.portfolio.data.PortfolioRepository
import com.taj.portfolio.data.WorkDetail
import com.taj.portfolio.data.WorkSummary
import com.taj.portfolio.data.cache.AppDatabase
import com.taj.portfolio.ui.theme.PortfolioTheme
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

        setContent {
            PortfolioTheme {
                val mainVm: MainViewModel = viewModel(factory = MainViewModelFactory(repository))
                App(mainVm, repository)
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
)

@Composable
private fun App(mainVm: MainViewModel, repository: PortfolioRepository) {
    val navController = rememberNavController()
    val state by mainVm.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { if (showBottomBar(navController)) BottomNavBar(navController) },
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
                    onOpenWork = { navController.navigate("work") },
                    onOpenWorkItem = { slug -> navController.navigate("work/$slug") },
                )
            }
            composable("work") {
                WorkScreen(
                    state = state,
                    onRetry = mainVm::refresh,
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
            composable("about") { AboutScreen(state = state, onRetry = mainVm::refresh) }
            composable("contact") { ContactScreen(state = state, onRetry = mainVm::refresh) }
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
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
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
    onOpenWork: () -> Unit,
    onOpenWorkItem: (String) -> Unit,
) {
    if (state.loading && state.profile == null) return FullScreenLoading("Loading portfolio...")
    if (state.error != null && state.profile == null) return FullScreenError(state.error, onRetry)

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                ) {
                    Text(
                        state.profile?.name.orEmpty(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(state.profile?.role.orEmpty(), style = MaterialTheme.typography.titleMedium)
                    Text(state.profile?.tagline.orEmpty(), modifier = Modifier.padding(top = 8.dp))
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

@Composable
private fun WorkScreen(
    state: MainUiState,
    onRetry: () -> Unit,
    onOpenWorkItem: (String) -> Unit,
) {
    if (state.loading && state.work.isEmpty()) return FullScreenLoading("Loading projects...")
    if (state.error != null && state.work.isEmpty()) return FullScreenError(state.error, onRetry)

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Work", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Case studies and architecture outcomes", style = MaterialTheme.typography.bodyMedium)
        }
        items(state.work) { item -> WorkCard(item = item, onClick = { onOpenWorkItem(item.slug) }) }
    }
}

@Composable
private fun WorkCard(item: WorkSummary, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AsyncImage(
                model = absoluteUrl(item.coverImageUrl),
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
            )
            Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(item.summary, style = MaterialTheme.typography.bodyMedium)
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item.tags.forEach { tag -> AssistChip(onClick = {}, label = { Text(tag) }) }
            }
        }
    }
}

@Composable
private fun WorkDetailScreen(slug: String, state: WorkDetailUiState, onRetry: () -> Unit, onBack: () -> Unit) {
    if (state.loading && state.item == null) return FullScreenLoading("Loading $slug...")
    if (state.error != null && state.item == null) return FullScreenError(state.error, onRetry)
    state.item?.let { WorkDetailContent(item = it, onBack = onBack) }
}

@Composable
private fun WorkDetailContent(item: WorkDetail, onBack: () -> Unit) {
    val html = remember(item.content.body) { markdownToHtml(item.content.body) }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onBack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                Spacer(Modifier.width(8.dp))
                Text("Back", color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(8.dp))
            Text(item.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(item.summary, style = MaterialTheme.typography.bodyLarge)
        }
        item {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item.tags.forEach { tag -> AssistChip(onClick = {}, label = { Text(tag) }) }
            }
        }
        item {
            val sections = item.sections
            if (sections?.context?.isNotBlank() == true) SectionCard("Context", sections.context)
            if (sections?.constraints?.isNotBlank() == true) SectionCard("Constraints", sections.constraints)
            if (sections?.approach?.isNotBlank() == true) SectionCard("Approach", sections.approach)
            if (sections?.outcome?.isNotBlank() == true) SectionCard("Outcome", sections.outcome)
            if (sections?.learnings?.isNotBlank() == true) SectionCard("Learnings", sections.learnings)
        }
        item {
            Text("Full Case Study", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            MarkdownView(html = html)
        }
    }
}

@Composable
private fun SectionCard(title: String, content: String) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun AboutScreen(state: MainUiState, onRetry: () -> Unit) {
    if (state.loading && state.about == null) return FullScreenLoading("Loading about...")
    if (state.error != null && state.about == null) return FullScreenError(state.error, onRetry)

    val about = state.about
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
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
    }
}

@Composable
private fun ContactScreen(state: MainUiState, onRetry: () -> Unit) {
    val context = LocalContext.current
    if (state.loading && state.contact == null) return FullScreenLoading("Loading contact...")
    if (state.error != null && state.contact == null) return FullScreenError(state.error, onRetry)

    val contact = state.contact
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Contact", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(contact?.email.orEmpty(), style = MaterialTheme.typography.bodyLarge)

        Button(onClick = {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${contact?.email.orEmpty()}"))
            runCatching { context.startActivity(intent) }
        }) {
            Icon(Icons.Default.Mail, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Email Me")
        }

        contact?.links?.forEach { link ->
            Text(
                link.label,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                    runCatching { context.startActivity(intent) }
                },
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

@Composable
private fun MarkdownView(html: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = false
                webViewClient = WebViewClient()
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(BuildConfig.API_BASE_URL, wrapHtml(html), "text/html", "utf-8", null)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(720.dp),
    )
}

private fun markdownToHtml(markdown: String): String {
    val extensions = listOf(TablesExtension.create())
    val parser = Parser.builder().extensions(extensions).build()
    val renderer = HtmlRenderer.builder().extensions(extensions).build()
    return renderer.render(parser.parse(markdown))
}

private fun wrapHtml(body: String): String {
    return """
        <html>
          <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <style>
              body { font-family: sans-serif; padding: 4px; line-height: 1.5; color: #121212; }
              img { max-width: 100%; height: auto; border-radius: 10px; }
              pre { white-space: pre-wrap; background: #f4f4f4; padding: 8px; border-radius: 8px; }
              code { font-family: monospace; }
              table { width: 100%; border-collapse: collapse; }
              th, td { border: 1px solid #ddd; padding: 6px; }
            </style>
          </head>
          <body>$body</body>
        </html>
    """.trimIndent()
}

private fun absoluteUrl(pathOrUrl: String): String {
    return if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
        pathOrUrl
    } else {
        BuildConfig.API_BASE_URL.removeSuffix("/") + "/" + pathOrUrl.removePrefix("/")
    }
}

@Composable
private fun showBottomBar(navController: NavHostController): Boolean {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route
    return route in setOf("home", "work", "about", "contact")
}

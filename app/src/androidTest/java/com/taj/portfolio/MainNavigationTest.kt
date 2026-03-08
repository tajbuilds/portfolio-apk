package com.taj.portfolio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainNavigationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setUp() {
        composeRule.setContent { TestNavigationHarness() }
    }

    @Test
    fun bottomNav_workTab_opensWorkScreen() {
        composeRule.onNodeWithTag("tab-work", useUnmergedTree = true).performClick()
        composeRule.onNodeWithTag("work-screen-title", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun workCard_openDetail_andBackReturnsToList() {
        composeRule.onNodeWithTag("tab-work", useUnmergedTree = true).performClick()
        composeRule.onNodeWithTag("work-list-card-0", useUnmergedTree = true).performClick()
        composeRule.onNodeWithTag("work-detail-title", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithTag("work-detail-back", useUnmergedTree = true).performClick()
        composeRule.onNodeWithTag("work-screen-title", useUnmergedTree = true).assertIsDisplayed()
    }
}

@Composable
private fun TestNavigationHarness() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { BottomNavBar(navController) }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("test-nav-host"),
        ) {
            composable("home") { Text("Home", modifier = Modifier.testTag("home-screen-title")) }
            composable("about") { Text("About") }
            composable("contact") { Text("Contact") }
            composable("settings") { Text("Settings") }
            composable("work") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { navController.navigate("work/demo") },
                ) {
                    Text("Work", modifier = Modifier.testTag("work-screen-title"))
                    Text(
                        "Card",
                        modifier = Modifier
                            .testTag("work-list-card-0")
                            .clickable { navController.navigate("work/demo") },
                    )
                }
            }
            composable("work/{slug}") {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Detail", modifier = Modifier.testTag("work-detail-title"))
                    Text(
                        "Back",
                        modifier = Modifier
                            .testTag("work-detail-back")
                            .clickable { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

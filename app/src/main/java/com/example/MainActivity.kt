package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.model.getStrings
import com.example.data.repository.CoupleRepository
import com.example.ui.screens.CycleScreen
import com.example.ui.screens.DatesScreen
import com.example.ui.screens.GamesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OurWorldScreen
import com.example.ui.theme.MYLYTheme
import com.example.ui.theme.NightObsidian
import com.example.ui.theme.NightSurface
import com.example.ui.theme.RosePrimary
import com.example.ui.viewmodel.CoupleViewModel
import com.example.ui.viewmodel.CoupleViewModelFactory
import com.example.ui.viewmodel.MainTab

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CoupleRepository(database)
        val factory = CoupleViewModelFactory(repository, applicationContext)
        val viewModel: CoupleViewModel by viewModels { factory }

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            MYLYTheme(darkTheme = isDarkTheme) {
                MylyApp(viewModel = viewModel, isDarkTheme = isDarkTheme)
            }
        }
    }
}

@Composable
fun MylyApp(viewModel: CoupleViewModel, isDarkTheme: Boolean) {
    val currentTab by viewModel.currentTab.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()
    val snackMessage by viewModel.snackMessage.collectAsState()
    val strings = getStrings(language)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = if (isDarkTheme) NightSurface else RosePrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = data.visuals.message, fontSize = 13.sp)
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                containerColor = if (isDarkTheme) NightSurface else Color.White,
                contentColor = RosePrimary
            ) {
                // Tab 1: Together
                NavigationBarItem(
                    selected = currentTab == MainTab.TOGETHER,
                    onClick = { viewModel.selectTab(MainTab.TOGETHER) },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = strings.tabTogether) },
                    label = { Text(strings.tabTogether, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RosePrimary,
                        selectedTextColor = RosePrimary,
                        indicatorColor = RosePrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_together")
                )

                // Tab 2: Games
                NavigationBarItem(
                    selected = currentTab == MainTab.GAMES,
                    onClick = { viewModel.selectTab(MainTab.GAMES) },
                    icon = { Icon(Icons.Default.Star, contentDescription = strings.tabGames) },
                    label = { Text(strings.tabGames, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RosePrimary,
                        selectedTextColor = RosePrimary,
                        indicatorColor = RosePrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_games")
                )

                // Tab 3: Cycle Care
                NavigationBarItem(
                    selected = currentTab == MainTab.CYCLE,
                    onClick = { viewModel.selectTab(MainTab.CYCLE) },
                    icon = { Icon(Icons.Default.Spa, contentDescription = strings.tabCycle) },
                    label = { Text(strings.tabCycle, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RosePrimary,
                        selectedTextColor = RosePrimary,
                        indicatorColor = RosePrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_cycle")
                )

                // Tab 4: Date Ideas
                NavigationBarItem(
                    selected = currentTab == MainTab.DATES,
                    onClick = { viewModel.selectTab(MainTab.DATES) },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = strings.tabDates) },
                    label = { Text(strings.tabDates, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RosePrimary,
                        selectedTextColor = RosePrimary,
                        indicatorColor = RosePrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_dates")
                )

                // Tab 5: Our World
                NavigationBarItem(
                    selected = currentTab == MainTab.OUR_WORLD,
                    onClick = { viewModel.selectTab(MainTab.OUR_WORLD) },
                    icon = { Icon(Icons.Default.Public, contentDescription = strings.tabOurWorld) },
                    label = { Text(strings.tabOurWorld, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RosePrimary,
                        selectedTextColor = RosePrimary,
                        indicatorColor = RosePrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("nav_tab_our_world")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    MainTab.TOGETHER -> HomeScreen(viewModel = viewModel)
                    MainTab.GAMES -> GamesScreen(viewModel = viewModel)
                    MainTab.CYCLE -> CycleScreen(viewModel = viewModel)
                    MainTab.DATES -> DatesScreen(viewModel = viewModel)
                    MainTab.OUR_WORLD -> OurWorldScreen(viewModel = viewModel)
                }
            }
        }
    }
}

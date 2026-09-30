package com.yodgorbek.securetunnel.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yodgorbek.securetunnel.feature.assistant.AssistantScreen
import com.yodgorbek.securetunnel.feature.assistant.AssistantViewModel
import com.yodgorbek.securetunnel.feature.diagnostics.DiagnosticsScreen
import com.yodgorbek.securetunnel.feature.diagnostics.DiagnosticsViewModel
import com.yodgorbek.securetunnel.feature.history.HistoryScreen
import com.yodgorbek.securetunnel.feature.history.HistoryViewModel
import com.yodgorbek.securetunnel.feature.home.HomeScreen
import com.yodgorbek.securetunnel.feature.home.HomeViewModel
import com.yodgorbek.securetunnel.feature.locations.LocationsScreen
import com.yodgorbek.securetunnel.feature.locations.LocationsViewModel
import com.yodgorbek.securetunnel.feature.settings.SettingsScreen
import com.yodgorbek.securetunnel.feature.settings.SettingsViewModel
import org.koin.compose.koinInject

@Composable
fun SecureTunnelNavHost(
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    val homeViewModel: HomeViewModel = koinInject()
    val locationsViewModel: LocationsViewModel = koinInject()
    val assistantViewModel: AssistantViewModel = koinInject()
    val historyViewModel: HistoryViewModel = koinInject()
    val diagnosticsViewModel: DiagnosticsViewModel = koinInject()
    val settingsViewModel: SettingsViewModel = koinInject()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (currentScreen.isBottomBarTab) {
                BottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { currentScreen = it }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Crossfade(targetState = currentScreen, label = "navigation_transition") { screen ->
                when (screen) {
                    Screen.Home -> HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToLocations = { currentScreen = Screen.Locations }
                    )
                    Screen.Locations -> LocationsScreen(
                        viewModel = locationsViewModel,
                        onBack = { currentScreen = Screen.Home }
                    )
                    Screen.Assistant -> AssistantScreen(
                        viewModel = assistantViewModel
                    )
                    Screen.Settings -> SettingsScreen(
                        viewModel = settingsViewModel,
                        onNavigateToDiagnostics = { currentScreen = Screen.Diagnostics },
                        onNavigateToHistory = { currentScreen = Screen.History }
                    )
                    Screen.Diagnostics -> DiagnosticsScreen(
                        viewModel = diagnosticsViewModel,
                        onBack = { currentScreen = Screen.Settings }
                    )
                    Screen.History -> HistoryScreen(
                        viewModel = historyViewModel,
                        onBack = { currentScreen = Screen.Settings }
                    )
                }
            }
        }
    }
}

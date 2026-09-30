package com.yodgorbek.securetunnel

import androidx.compose.runtime.Composable
import com.yodgorbek.securetunnel.designsystem.SecureTunnelTheme
import com.yodgorbek.securetunnel.di.sharedLogicModules
import com.yodgorbek.securetunnel.feature.assistant.AssistantViewModel
import com.yodgorbek.securetunnel.feature.diagnostics.DiagnosticsViewModel
import com.yodgorbek.securetunnel.feature.history.HistoryViewModel
import com.yodgorbek.securetunnel.feature.home.HomeViewModel
import com.yodgorbek.securetunnel.feature.locations.LocationsViewModel
import com.yodgorbek.securetunnel.feature.settings.SettingsViewModel
import com.yodgorbek.securetunnel.navigation.SecureTunnelNavHost
import org.koin.compose.KoinApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel {
        HomeViewModel(
            getVpnStatusUseCase = get(),
            getVpnStatisticsUseCase = get(),
            getActiveServerUseCase = get(),
            getRecentServersUseCase = get(),
            getRecommendedServerUseCase = get(),
            connectVpnUseCase = get(),
            disconnectVpnUseCase = get(),
            refreshVpnServersUseCase = get(),
            setSelectedServerUseCase = get()
        )
    }
    viewModel {
        LocationsViewModel(
            getVpnServersUseCase = get(),
            getActiveServerUseCase = get(),
            refreshVpnServersUseCase = get(),
            toggleFavoriteUseCase = get(),
            setSelectedServerUseCase = get(),
            filterAndSortServersUseCase = get(),
            getRecommendedServerUseCase = get()
        )
    }
    viewModel {
        AssistantViewModel(
            getAiConversationUseCase = get(),
            sendAiMessageUseCase = get(),
            clearAiConversationUseCase = get()
        )
    }
    viewModel {
        HistoryViewModel(
            getConnectionHistoryUseCase = get(),
            clearHistoryUseCase = get()
        )
    }
    viewModel {
        DiagnosticsViewModel(
            runDiagnosticsUseCase = get(),
            getDiagnosticResultsUseCase = get(),
            getActiveServerUseCase = get()
        )
    }
    viewModel {
        SettingsViewModel(
            getSettingsUseCase = get(),
            updateSettingsUseCase = get()
        )
    }
}

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(sharedLogicModules + uiModule)
        }
    ) {
        SecureTunnelTheme {
            SecureTunnelNavHost()
        }
    }
}
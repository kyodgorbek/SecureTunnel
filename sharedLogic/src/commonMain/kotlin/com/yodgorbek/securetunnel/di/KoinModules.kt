package com.yodgorbek.securetunnel.di

import com.yodgorbek.securetunnel.core.vpn.MockVpnEngine
import com.yodgorbek.securetunnel.core.vpn.VpnEngine
import com.yodgorbek.securetunnel.data.local.VpnStorage
import com.yodgorbek.securetunnel.data.remote.GroqApiClient
import com.yodgorbek.securetunnel.data.remote.VpnGateDataSource
import com.yodgorbek.securetunnel.data.remote.VpnGateDataSourceImpl
import com.yodgorbek.securetunnel.data.repository.AiRepositoryImpl
import com.yodgorbek.securetunnel.data.repository.DiagnosticsRepositoryImpl
import com.yodgorbek.securetunnel.data.repository.SettingsRepositoryImpl
import com.yodgorbek.securetunnel.data.repository.VpnRepositoryImpl
import com.yodgorbek.securetunnel.domain.repository.AiRepository
import com.yodgorbek.securetunnel.domain.repository.DiagnosticsRepository
import com.yodgorbek.securetunnel.domain.repository.SettingsRepository
import com.yodgorbek.securetunnel.domain.repository.VpnRepository
import com.yodgorbek.securetunnel.domain.usecase.ClearAiConversationUseCase
import com.yodgorbek.securetunnel.domain.usecase.ClearHistoryUseCase
import com.yodgorbek.securetunnel.domain.usecase.ConnectVpnUseCase
import com.yodgorbek.securetunnel.domain.usecase.DisconnectVpnUseCase
import com.yodgorbek.securetunnel.domain.usecase.FilterAndSortServersUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetActiveServerUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetAiConversationUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetConnectionHistoryUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetDiagnosticResultsUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetFavoriteServersUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetRecentServersUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetRecommendedServerUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetSettingsUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetVpnServersUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetVpnStatisticsUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetVpnStatusUseCase
import com.yodgorbek.securetunnel.domain.usecase.RefreshVpnServersUseCase
import com.yodgorbek.securetunnel.domain.usecase.RunDiagnosticsUseCase
import com.yodgorbek.securetunnel.domain.usecase.SendAiMessageUseCase
import com.yodgorbek.securetunnel.domain.usecase.SetSelectedServerUseCase
import com.yodgorbek.securetunnel.domain.usecase.ToggleFavoriteUseCase
import com.yodgorbek.securetunnel.domain.usecase.UpdateSettingsUseCase
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = false
                    }
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 10000
                socketTimeoutMillis = 15000
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }
    single<VpnGateDataSource> { VpnGateDataSourceImpl(httpClient = get()) }
    single { GroqApiClient(httpClient = get()) }
}

val storageModule = module {
    single { VpnStorage() }
}

val vpnEngineModule = module {
    // Default fallback VPN engine (platform modules override this with AndroidVpnEngine or IosVpnEngine)
    single<VpnEngine> { MockVpnEngine() }
}

val repositoryModule = module {
    single<VpnRepository> {
        VpnRepositoryImpl(
            vpnGateDataSource = get(),
            vpnStorage = get(),
            vpnEngine = get()
        )
    }
    single<AiRepository> {
        AiRepositoryImpl(
            groqApiClient = get(),
            vpnRepository = get(),
            vpnStorage = get()
        )
    }
    single<SettingsRepository> {
        SettingsRepositoryImpl(
            vpnStorage = get()
        )
    }
    single<DiagnosticsRepository> {
        DiagnosticsRepositoryImpl(
            httpClient = get()
        )
    }
}

val useCaseModule = module {
    factory { GetVpnServersUseCase(repository = get()) }
    factory { RefreshVpnServersUseCase(repository = get()) }
    factory { GetFavoriteServersUseCase(repository = get()) }
    factory { GetRecentServersUseCase(repository = get()) }
    factory { ToggleFavoriteUseCase(repository = get()) }
    factory { ConnectVpnUseCase(repository = get()) }
    factory { DisconnectVpnUseCase(repository = get()) }
    factory { GetVpnStatusUseCase(repository = get()) }
    factory { GetVpnStatisticsUseCase(repository = get()) }
    factory { GetActiveServerUseCase(repository = get()) }
    factory { SetSelectedServerUseCase(repository = get()) }
    factory { GetConnectionHistoryUseCase(repository = get()) }
    factory { ClearHistoryUseCase(repository = get()) }
    factory { GetRecommendedServerUseCase(repository = get()) }
    factory { FilterAndSortServersUseCase() }
    factory { GetAiConversationUseCase(repository = get()) }
    factory { SendAiMessageUseCase(repository = get()) }
    factory { ClearAiConversationUseCase(repository = get()) }
    factory { GetSettingsUseCase(repository = get()) }
    factory { UpdateSettingsUseCase(repository = get()) }
    factory { RunDiagnosticsUseCase(repository = get()) }
    factory { GetDiagnosticResultsUseCase(repository = get()) }
}

val sharedLogicModules: List<Module> = listOf(
    networkModule,
    storageModule,
    vpnEngineModule,
    repositoryModule,
    useCaseModule
)

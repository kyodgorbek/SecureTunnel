package com.yodgorbek.securetunnel.feature.home

import androidx.lifecycle.viewModelScope
import com.yodgorbek.securetunnel.core.common.MviViewModel
import com.yodgorbek.securetunnel.core.model.VpnStatus
import com.yodgorbek.securetunnel.domain.usecase.ConnectVpnUseCase
import com.yodgorbek.securetunnel.domain.usecase.DisconnectVpnUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetActiveServerUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetRecentServersUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetRecommendedServerUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetVpnStatisticsUseCase
import com.yodgorbek.securetunnel.domain.usecase.GetVpnStatusUseCase
import com.yodgorbek.securetunnel.domain.usecase.RefreshVpnServersUseCase
import com.yodgorbek.securetunnel.domain.usecase.SetSelectedServerUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getVpnStatusUseCase: GetVpnStatusUseCase,
    private val getVpnStatisticsUseCase: GetVpnStatisticsUseCase,
    private val getActiveServerUseCase: GetActiveServerUseCase,
    private val getRecentServersUseCase: GetRecentServersUseCase,
    private val getRecommendedServerUseCase: GetRecommendedServerUseCase,
    private val connectVpnUseCase: ConnectVpnUseCase,
    private val disconnectVpnUseCase: DisconnectVpnUseCase,
    private val refreshVpnServersUseCase: RefreshVpnServersUseCase,
    private val setSelectedServerUseCase: SetSelectedServerUseCase
) : MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {

    init {
        // Observe real VPN status
        getVpnStatusUseCase().onEach { status ->
            updateState { copy(status = status) }
        }.launchIn(viewModelScope)

        // Observe real-time traffic statistics
        getVpnStatisticsUseCase().onEach { stats ->
            updateState { copy(statistics = stats) }
        }.launchIn(viewModelScope)

        // Observe currently selected server
        getActiveServerUseCase().onEach { server ->
            updateState { copy(selectedServer = server) }
        }.launchIn(viewModelScope)

        // Observe recently connected servers
        getRecentServersUseCase().onEach { recents ->
            updateState { copy(recentServers = recents) }
        }.launchIn(viewModelScope)

        // Initial refresh of VPN Gate servers
        onIntent(HomeIntent.RefreshServers)
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.ToggleConnection -> handleToggleConnection()
            is HomeIntent.Disconnect -> handleDisconnect()
            is HomeIntent.SelectServer -> {
                setSelectedServerUseCase(intent.server)
                updateState { copy(selectedServer = intent.server) }
            }
            is HomeIntent.RefreshServers -> handleRefresh()
            is HomeIntent.DismissError -> updateState { copy(errorMessage = null) }
        }
    }

    private fun handleToggleConnection() {
        val currentStatus = currentState.status
        if (currentStatus is VpnStatus.Connected || currentStatus.isConnecting) {
            handleDisconnect()
        } else {
            val server = currentState.selectedServer
            if (server == null) {
                sendEffect(HomeEffect.ShowToast("Please select a VPN server first"))
                sendEffect(HomeEffect.NavigateToLocations)
                return
            }
            viewModelScope.launch {
                val result = connectVpnUseCase(server)
                result.onFailure { error ->
                    updateState { copy(errorMessage = error.message ?: "Connection failed") }
                }
            }
        }
    }

    private fun handleDisconnect() {
        viewModelScope.launch {
            disconnectVpnUseCase()
        }
    }

    private fun handleRefresh() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val result = refreshVpnServersUseCase()
            updateState { copy(isLoading = false) }
            result.onFailure {
                // Preserves cached servers offline
            }
        }
    }
}

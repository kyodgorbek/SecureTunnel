package com.yodgorbek.securetunnel.data.repository

import com.yodgorbek.securetunnel.core.model.ConnectionHistoryItem
import com.yodgorbek.securetunnel.core.model.VpnServer
import com.yodgorbek.securetunnel.core.model.VpnStatistics
import com.yodgorbek.securetunnel.core.model.VpnStatus
import com.yodgorbek.securetunnel.core.vpn.VpnEngine
import com.yodgorbek.securetunnel.data.local.VpnStorage
import com.yodgorbek.securetunnel.data.remote.VpnGateDataSource
import com.yodgorbek.securetunnel.domain.repository.VpnRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock

class VpnRepositoryImpl(
    private val vpnGateDataSource: VpnGateDataSource,
    private val vpnStorage: VpnStorage,
    private val vpnEngine: VpnEngine,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : VpnRepository {

    override val vpnStatus: Flow<VpnStatus> = vpnEngine.status
    override val vpnStatistics: Flow<VpnStatistics> = vpnEngine.statistics

    private val _selectedServer = MutableStateFlow<VpnServer?>(null)
    override val activeServer: Flow<VpnServer?> = _selectedServer.asStateFlow()

    private var connectionStartTime: Long = 0L
    private var lastKnownStats = VpnStatistics()

    init {
        // Observe statistics
        vpnEngine.statistics.onEach { stats ->
            lastKnownStats = stats
        }.launchIn(scope)

        // Observe connection lifecycle to record history
        vpnEngine.status.onEach { status ->
            when (status) {
                is VpnStatus.Connected -> {
                    connectionStartTime = Clock.System.now().toEpochMilliseconds()
                    val server = _selectedServer.value
                    if (server != null) {
                        vpnStorage.addRecentServer(server.id)
                    }
                }
                is VpnStatus.Disconnected -> {
                    if (connectionStartTime > 0L) {
                        val endTime = Clock.System.now().toEpochMilliseconds()
                        val duration = (endTime - connectionStartTime) / 1000L
                        val server = _selectedServer.value
                        if (server != null && duration >= 2) {
                            vpnStorage.addHistoryItem(
                                ConnectionHistoryItem(
                                    id = "hist_${endTime}",
                                    serverId = server.id,
                                    serverHostname = server.hostname,
                                    country = server.country,
                                    countryCode = server.countryCode,
                                    city = server.city,
                                    startedAt = connectionStartTime,
                                    endedAt = endTime,
                                    durationSeconds = maxOf(duration, lastKnownStats.durationSeconds),
                                    totalBytesIn = lastKnownStats.bytesIn,
                                    totalBytesOut = lastKnownStats.bytesOut,
                                    disconnectReason = "User Disconnected"
                                )
                            )
                        }
                        connectionStartTime = 0L
                    }
                }
                is VpnStatus.Failed -> {
                    connectionStartTime = 0L
                }
                else -> Unit
            }
        }.launchIn(scope)
    }

    override val servers: Flow<List<VpnServer>> = combine(
        vpnStorage.cachedServers,
        vpnStorage.favoriteIds,
        vpnStorage.recentIds
    ) { cached, favorites, recents ->
        cached.map { server ->
            server.copy(
                isFavorite = favorites.contains(server.id),
                isRecent = recents.contains(server.id)
            )
        }
    }

    override val favoriteServers: Flow<List<VpnServer>> = servers.combine(vpnStorage.favoriteIds) { all, favs ->
        all.filter { favs.contains(it.id) }
    }

    override val recentServers: Flow<List<VpnServer>> = servers.combine(vpnStorage.recentIds) { all, recents ->
        recents.mapNotNull { recentId -> all.find { it.id == recentId } }
    }

    override val connectionHistory: Flow<List<ConnectionHistoryItem>> = vpnStorage.connectionHistory

    override suspend fun refreshServers(): Result<List<VpnServer>> {
        val result = vpnGateDataSource.getServers()
        result.onSuccess { freshServers ->
            vpnStorage.updateCachedServers(freshServers)
            if (_selectedServer.value == null && freshServers.isNotEmpty()) {
                // Auto-pick first recommended server
                _selectedServer.value = freshServers.minByOrNull { it.ping ?: 999 } ?: freshServers.first()
            }
        }
        return result
    }

    override suspend fun connect(server: VpnServer): Result<Unit> {
        _selectedServer.value = server
        return vpnEngine.connect(server)
    }

    override suspend fun disconnect(): Result<Unit> {
        return vpnEngine.disconnect()
    }

    override suspend fun toggleFavorite(serverId: String) {
        vpnStorage.toggleFavorite(serverId)
    }

    override suspend fun clearHistory() {
        vpnStorage.clearHistory()
    }

    override fun getSelectedServer(): VpnServer? {
        return _selectedServer.value
    }

    override fun setSelectedServer(server: VpnServer) {
        _selectedServer.value = server
    }
}

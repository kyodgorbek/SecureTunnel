package com.yodgorbek.securetunnel.domain.usecase

import com.yodgorbek.securetunnel.core.model.ConnectionHistoryItem
import com.yodgorbek.securetunnel.core.model.ServerFilter
import com.yodgorbek.securetunnel.core.model.ServerSortOption
import com.yodgorbek.securetunnel.core.model.VpnServer
import com.yodgorbek.securetunnel.core.model.VpnStatistics
import com.yodgorbek.securetunnel.core.model.VpnStatus
import com.yodgorbek.securetunnel.domain.repository.VpnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetVpnServersUseCase(private val repository: VpnRepository) {
    operator fun invoke(): Flow<List<VpnServer>> = repository.servers
}

class RefreshVpnServersUseCase(private val repository: VpnRepository) {
    suspend operator fun invoke(): Result<List<VpnServer>> = repository.refreshServers()
}

class GetFavoriteServersUseCase(private val repository: VpnRepository) {
    operator fun invoke(): Flow<List<VpnServer>> = repository.favoriteServers
}

class GetRecentServersUseCase(private val repository: VpnRepository) {
    operator fun invoke(): Flow<List<VpnServer>> = repository.recentServers
}

class ToggleFavoriteUseCase(private val repository: VpnRepository) {
    suspend operator fun invoke(serverId: String) = repository.toggleFavorite(serverId)
}

class ConnectVpnUseCase(private val repository: VpnRepository) {
    suspend operator fun invoke(server: VpnServer): Result<Unit> = repository.connect(server)
}

class DisconnectVpnUseCase(private val repository: VpnRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.disconnect()
}

class GetVpnStatusUseCase(private val repository: VpnRepository) {
    operator fun invoke(): Flow<VpnStatus> = repository.vpnStatus
}

class GetVpnStatisticsUseCase(private val repository: VpnRepository) {
    operator fun invoke(): Flow<VpnStatistics> = repository.vpnStatistics
}

class GetActiveServerUseCase(private val repository: VpnRepository) {
    operator fun invoke(): Flow<VpnServer?> = repository.activeServer
}

class SetSelectedServerUseCase(private val repository: VpnRepository) {
    operator fun invoke(server: VpnServer) = repository.setSelectedServer(server)
}

class GetConnectionHistoryUseCase(private val repository: VpnRepository) {
    operator fun invoke(): Flow<List<ConnectionHistoryItem>> = repository.connectionHistory
}

class ClearHistoryUseCase(private val repository: VpnRepository) {
    suspend operator fun invoke() = repository.clearHistory()
}

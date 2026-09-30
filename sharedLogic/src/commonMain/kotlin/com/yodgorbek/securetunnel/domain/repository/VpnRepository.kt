package com.yodgorbek.securetunnel.domain.repository

import com.yodgorbek.securetunnel.core.model.ConnectionHistoryItem
import com.yodgorbek.securetunnel.core.model.VpnServer
import com.yodgorbek.securetunnel.core.model.VpnStatistics
import com.yodgorbek.securetunnel.core.model.VpnStatus
import kotlinx.coroutines.flow.Flow

interface VpnRepository {
    val vpnStatus: Flow<VpnStatus>
    val vpnStatistics: Flow<VpnStatistics>
    val activeServer: Flow<VpnServer?>
    val servers: Flow<List<VpnServer>>
    val favoriteServers: Flow<List<VpnServer>>
    val recentServers: Flow<List<VpnServer>>
    val connectionHistory: Flow<List<ConnectionHistoryItem>>

    suspend fun refreshServers(): Result<List<VpnServer>>
    suspend fun connect(server: VpnServer): Result<Unit>
    suspend fun disconnect(): Result<Unit>
    suspend fun toggleFavorite(serverId: String)
    suspend fun clearHistory()
    fun getSelectedServer(): VpnServer?
    fun setSelectedServer(server: VpnServer)
}

package com.yodgorbek.securetunnel.data.local

import com.yodgorbek.securetunnel.core.model.AppSettings
import com.yodgorbek.securetunnel.core.model.ConnectionHistoryItem
import com.yodgorbek.securetunnel.core.model.VpnServer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * High-performance, offline-first local storage for VPN servers, favorites, history, and settings.
 */
class VpnStorage {

    private val _cachedServers = MutableStateFlow<List<VpnServer>>(emptyList())
    val cachedServers: Flow<List<VpnServer>> = _cachedServers.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: Flow<Set<String>> = _favoriteIds.asStateFlow()

    private val _recentIds = MutableStateFlow<List<String>>(emptyList())
    val recentIds: Flow<List<String>> = _recentIds.asStateFlow()

    private val _connectionHistory = MutableStateFlow<List<ConnectionHistoryItem>>(emptyList())
    val connectionHistory: Flow<List<ConnectionHistoryItem>> = _connectionHistory.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: Flow<AppSettings> = _settings.asStateFlow()

    fun updateCachedServers(servers: List<VpnServer>) {
        _cachedServers.value = servers
    }

    fun toggleFavorite(serverId: String) {
        _favoriteIds.update { set ->
            if (set.contains(serverId)) set - serverId else set + serverId
        }
    }

    fun addRecentServer(serverId: String) {
        _recentIds.update { list ->
            val updated = list.toMutableList()
            updated.remove(serverId)
            updated.add(0, serverId)
            if (updated.size > 10) updated.take(10) else updated
        }
    }

    fun addHistoryItem(item: ConnectionHistoryItem) {
        _connectionHistory.update { list ->
            listOf(item) + list.take(49)
        }
    }

    fun clearHistory() {
        _connectionHistory.value = emptyList()
    }

    fun updateSettings(reducer: AppSettings.() -> AppSettings) {
        _settings.update(reducer)
    }

    fun getSettings(): AppSettings = _settings.value
}

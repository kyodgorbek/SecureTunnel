package com.yodgorbek.securetunnel.domain.usecase

import com.yodgorbek.securetunnel.core.model.ServerFilter
import com.yodgorbek.securetunnel.core.model.ServerSortOption
import com.yodgorbek.securetunnel.core.model.VpnServer
import com.yodgorbek.securetunnel.domain.repository.VpnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Recommends the optimal VPN server based on measurable criteria:
 * Low latency (ping), high bandwidth (speed), low active sessions, and uptime.
 */
class GetRecommendedServerUseCase(private val repository: VpnRepository) {

    operator fun invoke(): Flow<VpnServer?> {
        return repository.servers.map { servers ->
            calculateRecommended(servers)
        }
    }

    fun calculateRecommended(servers: List<VpnServer>): VpnServer? {
        if (servers.isEmpty()) return null
        return servers.maxByOrNull { server ->
            computeServerScore(server)
        }
    }

    private fun computeServerScore(server: VpnServer): Double {
        val ping = server.ping ?: 120L
        val speedMbps = (server.speed ?: 5_000_000L) / 1_000_000.0
        val sessions = server.sessions
        val uptimeHours = (server.uptime ?: 3600L) / 3600.0

        // Higher speed is good (+), lower ping is crucial (-), lower sessions is better (-)
        val pingScore = (300.0 - ping.coerceIn(5, 400)) * 2.0
        val speedScore = speedMbps.coerceIn(1.0, 150.0) * 1.5
        val loadScore = (100.0 - sessions.coerceIn(0, 100)) * 0.8
        val stabilityScore = uptimeHours.coerceIn(1.0, 100.0) * 0.2

        return pingScore + speedScore + loadScore + stabilityScore
    }
}

class FilterAndSortServersUseCase {

    operator fun invoke(
        servers: List<VpnServer>,
        filter: ServerFilter
    ): List<VpnServer> {
        var filtered = servers

        // 1. Search Query
        if (filter.searchQuery.isNotBlank()) {
            val q = filter.searchQuery.trim().lowercase()
            filtered = filtered.filter { server ->
                server.country.lowercase().contains(q) ||
                        server.countryCode.lowercase().contains(q) ||
                        (server.city?.lowercase()?.contains(q) == true) ||
                        server.hostname.lowercase().contains(q) ||
                        (server.ipAddress?.contains(q) == true)
            }
        }

        // 2. Country Filter
        if (!filter.selectedCountryCode.isNullOrBlank()) {
            filtered = filtered.filter { it.countryCode.equals(filter.selectedCountryCode, ignoreCase = true) }
        }

        // 3. Max Ping
        if (filter.maxPingMs != null) {
            filtered = filtered.filter { (it.ping ?: 999) <= filter.maxPingMs }
        }

        // 4. Min Speed
        if (filter.minSpeedMbps != null) {
            val minBytes = (filter.minSpeedMbps * 1_000_000).toLong()
            filtered = filtered.filter { (it.speed ?: 0) >= minBytes }
        }

        // 5. OpenVPN Only
        if (filter.openVpnOnly) {
            filtered = filtered.filter { it.openVpnSupported }
        }

        // 6. Favorites
        if (filter.favoritesOnly) {
            filtered = filtered.filter { it.isFavorite }
        }

        // 7. Recent
        if (filter.recentOnly) {
            filtered = filtered.filter { it.isRecent }
        }

        // 8. Sorting
        return when (filter.sortOption) {
            ServerSortOption.RECOMMENDED -> {
                filtered.sortedByDescending { s ->
                    val ping = (s.ping ?: 150L).toDouble()
                    val speed = (s.speed ?: 10_000_000L).toDouble() / 1_000_000.0
                    (speed * 2.0) - (ping * 1.5) - (s.sessions * 0.5)
                }
            }
            ServerSortOption.LOWEST_PING -> filtered.sortedBy { it.ping ?: 999L }
            ServerSortOption.HIGHEST_SPEED -> filtered.sortedByDescending { it.speed ?: 0L }
            ServerSortOption.LEAST_SESSIONS -> filtered.sortedBy { it.sessions }
            ServerSortOption.COUNTRY_NAME -> filtered.sortedBy { it.country }
        }
    }
}

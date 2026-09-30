package com.yodgorbek.securetunnel.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ServerSortOption {
    RECOMMENDED,
    LOWEST_PING,
    HIGHEST_SPEED,
    LEAST_SESSIONS,
    COUNTRY_NAME
}

@Serializable
data class ServerFilter(
    val searchQuery: String = "",
    val selectedCountryCode: String? = null,
    val maxPingMs: Long? = null,
    val minSpeedMbps: Double? = null,
    val openVpnOnly: Boolean = true,
    val favoritesOnly: Boolean = false,
    val recentOnly: Boolean = false,
    val sortOption: ServerSortOption = ServerSortOption.RECOMMENDED
)

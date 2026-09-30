package com.yodgorbek.securetunnel.core.model

import kotlinx.serialization.Serializable

/**
 * Historical record of a completed or disconnected VPN session.
 */
@Serializable
data class ConnectionHistoryItem(
    val id: String,
    val serverId: String,
    val serverHostname: String,
    val country: String,
    val countryCode: String,
    val city: String?,
    val startedAt: Long,
    val endedAt: Long,
    val durationSeconds: Long,
    val totalBytesIn: Long,
    val totalBytesOut: Long,
    val disconnectReason: String? = null
) {
    val flagEmoji: String
        get() = VpnServer.countryCodeToEmoji(countryCode)

    val formattedDuration: String
        get() {
            val hours = durationSeconds / 3600
            val minutes = (durationSeconds % 3600) / 60
            val seconds = durationSeconds % 60
            return if (hours > 0) {
                "${hours}h ${minutes}m"
            } else if (minutes > 0) {
                "${minutes}m ${seconds}s"
            } else {
                "${seconds}s"
            }
        }

    val formattedTotalData: String
        get() = VpnStatistics.formatBytes(totalBytesIn + totalBytesOut)
}

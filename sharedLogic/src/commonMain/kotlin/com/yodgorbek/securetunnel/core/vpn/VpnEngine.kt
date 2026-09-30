package com.yodgorbek.securetunnel.core.vpn

import com.yodgorbek.securetunnel.core.model.VpnServer
import com.yodgorbek.securetunnel.core.model.VpnStatistics
import com.yodgorbek.securetunnel.core.model.VpnStatus
import kotlinx.coroutines.flow.Flow

/**
 * Platform-agnostic interface for controlling the VPN connection lifecycle and streaming metrics.
 */
interface VpnEngine {

    /**
     * Observable stream of real connection lifecycle status.
     */
    val status: Flow<VpnStatus>

    /**
     * Observable stream of live traffic statistics (upload/download speed, bytes, duration).
     */
    val statistics: Flow<VpnStatistics>

    /**
     * Initiates a secure tunnel connection to the specified server.
     */
    suspend fun connect(server: VpnServer): Result<Unit>

    /**
     * Disconnects the active VPN tunnel.
     */
    suspend fun disconnect(): Result<Unit>
}

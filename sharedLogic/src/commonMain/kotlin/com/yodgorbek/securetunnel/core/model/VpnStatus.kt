package com.yodgorbek.securetunnel.core.model

import kotlinx.serialization.Serializable

/**
 * Real lifecycle status of the VPN connection.
 */
@Serializable
sealed interface VpnStatus {

    @Serializable
    data object Disconnected : VpnStatus

    @Serializable
    data object Preparing : VpnStatus

    @Serializable
    data object Connecting : VpnStatus

    @Serializable
    data class Connected(
        val serverId: String,
        val connectedAt: Long
    ) : VpnStatus

    @Serializable
    data object Disconnecting : VpnStatus

    @Serializable
    data class Failed(
        val error: VpnError
    ) : VpnStatus

    val isConnected: Boolean
        get() = this is Connected

    val isConnecting: Boolean
        get() = this is Connecting || this is Preparing

    val isDisconnected: Boolean
        get() = this is Disconnected || this is Failed
}

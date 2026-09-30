package com.yodgorbek.securetunnel.core.model

import kotlinx.serialization.Serializable

/**
 * Categorized errors during VPN lifecycle and connection handshakes.
 */
@Serializable
sealed interface VpnError {

    @Serializable
    data object PermissionRequired : VpnError

    @Serializable
    data object PermissionDenied : VpnError

    @Serializable
    data object NetworkUnavailable : VpnError

    @Serializable
    data object ServerUnavailable : VpnError

    @Serializable
    data object InvalidConfiguration : VpnError

    @Serializable
    data object ConnectionTimeout : VpnError

    @Serializable
    data object TunnelCreationFailed : VpnError

    @Serializable
    data object AuthenticationFailed : VpnError

    @Serializable
    data object UnsupportedServer : VpnError

    @Serializable
    data class Unknown(
        val message: String
    ) : VpnError

    val userMessage: String
        get() = when (this) {
            is PermissionRequired -> "VPN permission is required to create a secure tunnel."
            is PermissionDenied -> "VPN permission was denied by the system."
            is NetworkUnavailable -> "No internet connection detected. Please check Wi-Fi or mobile data."
            is ServerUnavailable -> "The selected volunteer relay is currently unreachable or offline."
            is InvalidConfiguration -> "The OpenVPN configuration provided by the server is invalid."
            is ConnectionTimeout -> "Connection timed out while negotiating secure handshake."
            is TunnelCreationFailed -> "Failed to initialize local network tunnel interface."
            is AuthenticationFailed -> "Relay server rejected the anonymous connection certificate."
            is UnsupportedServer -> "This server does not support OpenVPN UDP/TCP protocols."
            is Unknown -> message.ifBlank { "An unexpected error occurred during connection." }
        }
}

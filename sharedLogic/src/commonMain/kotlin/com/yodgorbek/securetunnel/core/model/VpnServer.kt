package com.yodgorbek.securetunnel.core.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a VPN relay server (sourced from VPN Gate or custom API).
 */
@Serializable
data class VpnServer(
    val id: String,
    val hostname: String,
    val ipAddress: String?,
    val country: String,
    val countryCode: String,
    val city: String?,
    val sessions: Int,
    val uptime: Long?,
    val speed: Long?,
    val ping: Long?,
    val score: Long?,
    val openVpnSupported: Boolean,
    val configurationUrl: String? = null,
    val openVpnConfigDataBase64: String? = null,
    val port: Int = 1194,
    val proto: String = "udp",
    val isFavorite: Boolean = false,
    val isRecent: Boolean = false,
    val isVolunteer: Boolean = true,
    val operatorName: String? = null
) {
    val displayCountry: String
        get() = country.ifBlank { "Unknown Location" }

    val displayCity: String
        get() = city ?: "Relay $hostname"

    val displayPing: String
        get() = if (ping != null && ping > 0) "${ping} ms" else "-- ms"

    val displaySpeedMbps: String
        get() = if (speed != null && speed > 0) {
            val tenths = speed / 100_000L
            val mbps = tenths / 10.0
            "$mbps Mbps"
        } else {
            "-- Mbps"
        }

    val flagEmoji: String
        get() = countryCodeToEmoji(countryCode)

    companion object {
        fun countryCodeToEmoji(countryCode: String): String {
            if (countryCode.length != 2) return "🌐"
            val code = countryCode.uppercase()
            val c1 = code[0]
            val c2 = code[1]
            if (c1 !in 'A'..'Z' || c2 !in 'A'..'Z') return "🌐"
            val first = 0x1F1E6 + (c1.code - 'A'.code)
            val second = 0x1F1E6 + (c2.code - 'A'.code)
            return buildString {
                appendCodePoint(first)
                appendCodePoint(second)
            }
        }

        private fun StringBuilder.appendCodePoint(codePoint: Int) {
            val high = ((codePoint - 0x10000) ushr 10) + 0xD800
            val low = ((codePoint - 0x10000) and 0x3FF) + 0xDC00
            append(high.toChar())
            append(low.toChar())
        }
    }
}
